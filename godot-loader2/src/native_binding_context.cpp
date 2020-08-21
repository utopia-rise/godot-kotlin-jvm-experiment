#include <iostream>
#include <cassert>
#include "native_binding_context.h"
#include "jni_utils.h"

jni::JObject getCurrentThread(jni::Env& env);
jni::JObject createClassLoader(jni::Env& env, const std::string& bootstrapJar);
void setContextClassLoader(jni::Env& env, jni::JObject thread, jni::JObject classLoader);

JClassHelper NativeBindingContext::JH = JClassHelper("godot.internal.BindingContext");

NativeBindingContext& NativeBindingContext::instance() {
    static NativeBindingContext bindingContext;
    return bindingContext;
}

void NativeBindingContext::bind(godot_object *library, const std::string& libraryPath) {
    this->library = library;
    this->libraryPath = libraryPath;
    projectDir = libraryPath.substr(0, libraryPath.find_last_of('/') + 1);
    std::cout << "project_dir: " << projectDir << std::endl;
    auto args = jni::InitArgs();
    args.option("-Xcheck:jni");
    args.option("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005");
    jni::Jvm::init(args);

    auto bootstrapJar = std::string(projectDir);
    bootstrapJar.append("build/libs/bootstrap.jar");
    startScope();
    auto& env = jni::Jvm::currentEnv();
    // set class loader here
    std::cout << "Creating class loader to load " << bootstrapJar << std::endl;
    classLoader = createClassLoader(env, bootstrapJar).newGlobalRef(env);
    std::cout << "Setting context class loader for current thread" << std::endl;
    auto thread = getCurrentThread(env);
    setContextClassLoader(env, thread, classLoader);

    NativeTransferContext::registerNatives(env, classLoader);

    auto cls = JH.getClass(env, classLoader);
    auto instanceField = cls.getStaticFieldId(env, "INSTANCE", "Lgodot/internal/BindingContext;");
    wrapped = cls.getStaticObjectField(env, instanceField).newGlobalRef(env);
    assert(!wrapped.isNull());

    // get java TransferContext
    auto getTransferContextMethod = JH.getMethodId(env, classLoader, "getTransferContext", "()Lgodot/wire/TransferContext;");
    auto javaTransferContext = wrapped.callObjectMethod(env, getTransferContextMethod);
    assert(!javaTransferContext.isNull());
    transferContext.init(env, javaTransferContext);
    endScope();
}

void NativeBindingContext::unbind(bool destroyJvm) {
    startScope();
    auto& env = jni::Jvm::currentEnv();
    transferContext.dispose(env);
    wrapped.deleteGlobalRef(env);
    classLoader.deleteGlobalRef(env);
    // reset all java caches
    JClassHelper::reset();
    endScope();
    this->library = nullptr;
    if (destroyJvm) {
        jni::Jvm::destroy();
    }
}

void NativeBindingContext::startScope() {
    auto& env = jni::Jvm::currentEnv();
    env.pushLocalFrame(100);
}

void NativeBindingContext::endScope() {
    auto& env = jni::Jvm::currentEnv();
    env.popLocalFrame();
}

std::vector<NativeClassHandle*> getClasses(jni::Env& env, jni::JObject classLoader) {
    auto cls = loadClass(env, classLoader, "godot.Entry");
    if (cls.isNull()) {
        throw std::runtime_error("Failed to load godot.Entry class, does it exist?");
    }
    auto ctor = cls.getConstructorMethodId(env, "()V");
    auto instance = cls.newInstance(env, ctor);
    auto initMethod = cls.getMethodId(env, "init", "()[Lgodot/registry/ClassHandle;");
    auto handles = jni::JObjectArray((jobjectArray) instance.callObjectMethod(env, initMethod).obj);
    auto nativeHandles = std::vector<NativeClassHandle*>();
    for (auto i = 0; i < handles.length(env); i++) {
        auto nativeHandle = new NativeClassHandle();
        nativeHandle->init(env, handles.get(env, i));
        nativeHandles.emplace_back(nativeHandle);
    }
    return nativeHandles;
}

void NativeBindingContext::registerClasses(void* nativescriptHandle) {
    startScope();
    auto& env = jni::Jvm::currentEnv();
    auto classHandles = getClasses(env, classLoader);
    for (auto handle : classHandles) {
        handle->registerClass(env, classLoader, nativescriptHandle);
    }
    endScope();
}

void NativeBindingContext::unRegisterClasses(void* nativescriptHandle) {
    startScope();
    endScope();
}

jni::JObject getCurrentThread(jni::Env& env) {
    auto cls = env.findClass("java/lang/Thread");
    auto currentThreadMethodId = cls.getStaticMethodId(env, "currentThread", "()Ljava/lang/Thread;");
    auto thread = cls.callStaticObjectMethod(env, currentThreadMethodId);
    assert(!thread.isNull());
    return thread;
}

jni::JObject toJavaUrl(jni::Env& env, const std::string& bootstrapJar) {
    auto cls = env.findClass("java/io/File");
    auto ctor = cls.getConstructorMethodId(env, "(Ljava/lang/String;)V");
    auto path = env.newString(bootstrapJar.c_str());
    auto file = cls.newInstance(env, ctor, {path});
    assert(!file.isNull());
    auto toURLMethod = cls.getMethodId(env, "toURL", "()Ljava/net/URL;");
    auto url = file.callObjectMethod(env, toURLMethod);
    assert(!url.isNull());
    return url;
}

jni::JObject createClassLoader(jni::Env& env, const std::string& bootstrapJar) {
    auto url = toJavaUrl(env, bootstrapJar);
    auto urlCls = env.findClass("java/net/URL");
    auto urls = urlCls.newObjectArray(env, 1, {url});
    auto classLoaderCls = env.findClass("java/net/URLClassLoader");
    auto ctor = classLoaderCls.getConstructorMethodId(env, "([Ljava/net/URL;)V");
    auto classLoader = classLoaderCls.newInstance(env, ctor, {urls});
    assert(!classLoaderCls.isNull());
    return classLoader;
}

void setContextClassLoader(jni::Env& env, jni::JObject thread, jni::JObject classLoader) {
    auto cls = env.findClass("java/lang/Thread");
    auto setContextClassLoaderMethod = cls.getMethodId(env, "setContextClassLoader", "(Ljava/lang/ClassLoader;)V");
    thread.callObjectMethod(env, setContextClassLoaderMethod, {classLoader});
}
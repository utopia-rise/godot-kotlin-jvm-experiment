#include <iostream>
#include <cassert>
#include "native_binding_context.h"

jni::JObject getCurrentThread(jni::Env& env);
jni::JObject createClassLoader(jni::Env& env, const std::string& bootstrapJar);
void setContextClassLoader(jni::Env& env, jni::JObject thread, jni::JObject classLoader);

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
    args.option("-verbose:jni");
    jni::Jvm::init(args);

    auto bootstrapJar = std::string(projectDir);
    bootstrapJar.append("bootstrap.jar");
    startScope();
    auto env = jni::Jvm::currentEnv();
    // set class loader here
    std::cout << "Creating class loader to load " << bootstrapJar << std::endl;
    classLoader = createClassLoader(env, bootstrapJar);
    std::cout << "Setting context class loader for current thread" << std::endl;
    auto thread = getCurrentThread(env);
    setContextClassLoader(env, thread, classLoader);
    endScope();
}

void NativeBindingContext::unbind() {
    this->library = nullptr;
    jni::Jvm::destroy();
}

void NativeBindingContext::startScope() {
    auto env = jni::Jvm::currentEnv();
    env.pushLocalFrame(100);
}

void NativeBindingContext::endScope() {
    auto env = jni::Jvm::currentEnv();
    env.popLocalFrame();
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
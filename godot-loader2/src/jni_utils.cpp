#include "jni_utils.h"

jni::JClass loadClass(jni::Env& env, jni::JObject classLoader, const char* name) {
    static jmethodID loadClassMethodId;

    if (loadClassMethodId == nullptr) {
        auto cls = env.findClass("java/lang/ClassLoader");
        loadClassMethodId = cls.getMethodId(env, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    }
    auto str = env.newString(name);
    auto ret = classLoader.callObjectMethod(env, loadClassMethodId, {str});
    return jni::JClass((jclass) ret.obj);
}

static std::vector<JClassHelper*> INSTANCES = {};

void JClassHelper::reset() {
    // clear the cache, specially with jClass as it was loaded by the previousClassloader
    // which can cause class cast exception. Remember a Foo.class != Foo.class if they are loaded
    // by different class loaders.
    for (auto instance : INSTANCES) {
        instance->jClass = jni::JClass(nullptr);
        instance->methodIdCache.clear();
        instance->staticMethodIdCache.clear();
    }
}

JClassHelper::JClassHelper(const char* binaryName) {
    this->binaryName = std::string(binaryName);
    INSTANCES.emplace_back(this);
}

jni::JClass JClassHelper::getClass(jni::Env& env, jni::JObject classLoader) {
    if (jClass.isNull()) {
        jClass.obj = loadClass(env, classLoader, binaryName.c_str()).newGlobalRef(env).obj;
    }
    return jClass;
}

jni::MethodId JClassHelper::getMethodId(jni::Env& env, jni::JObject classLoader, const char* name, const char* signature) {
    auto key = std::string();
    key.append(name);
    key.append(signature);
    if (methodIdCache.find(key) != methodIdCache.end()) {
        return methodIdCache[key];
    }

    auto cls = getClass(env, classLoader);
    auto method = cls.getMethodId(env, name, signature);
    methodIdCache[key] = method;
    return method;
}

jni::MethodId JClassHelper::getStaticMethodId(jni::Env& env, jni::JObject classLoader, const char* name, const char* signature) {
    auto key = std::string();
    key.append(name);
    key.append(signature);
    if (staticMethodIdCache.find(key) != staticMethodIdCache.end()) {
        return staticMethodIdCache[key];
    }

    auto cls = getClass(env, classLoader);
    auto method = cls.getStaticMethodId(env, name, signature);
    staticMethodIdCache[key] = method;
    return method;
}

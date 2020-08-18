#ifndef GODOT_LOADER_JNI_UTILS_H
#define GODOT_LOADER_JNI_UTILS_H
#include <jvm.h>
#include <map>

jni::JClass loadClass(jni::Env& env, jni::JObject classLoader, const char* name);

class JClassHelper {
public:
    std::string binaryName;
    std::string sgn;

    JClassHelper(const char* binaryName);

    jni::MethodId getMethodId(jni::Env& env, jni::JObject classLoader, const char* name, const char* signature);
    jni::MethodId getStaticMethodId(jni::Env& env, jni::JObject classLoader, const char* name, const char* signature);

    jni::JClass getClass(jni::Env& env, jni::JObject classLoader);
private:
    jni::JClass jClass = jni::JClass(nullptr);
    std::map<std::string, jni::MethodId> methodIdCache;
    std::map<std::string, jni::MethodId> staticMethodIdCache;
};

#endif //GODOT_LOADER_JNI_UTILS_H

#include "jni_utils.h"

jni::JClass loadClass(jni::Env& env, jni::JObject classLoader, const char* name) {
    static jmethodID loadClassMethodId;

    if (loadClassMethodId == nullptr) {
        auto cls = env.findClass("java/lang/ClassLoader");
        loadClassMethodId = cls.getMethodId(env, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    }
    auto str = env.newString(name);
    auto ret = classLoader.callObjectMethod(env, loadClassMethodId, {str});
    return {(jclass) ret.obj};
}
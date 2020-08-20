#ifndef GODOT_LOADER_NATIVE_KFUNCTION_H
#define GODOT_LOADER_NATIVE_KFUNCTION_H
#include <jvm.h>
#include "jni_utils.h"
#include "native_kobject.h"
#include "native_tvalue.h"

class NativeKFunction {
public:
    NativeKFunction() = default;
    NativeKFunction(const NativeKFunction&) = delete;
    void operator=(const NativeKFunction&) = delete;

    void init(jni::Env& env, jni::JObject object);
    void dispose(jni::Env& env);

    static JClassHelper JH;
    int getParameterCount(jni::Env& env, jni::JObject classLoader);
    const std::string& getName(jni::Env& env, jni::JObject classLoader);
    const std::string& getRegistrationName(jni::Env& env, jni::JObject classLoader);

    // todo return type and args
    NativeTValue invoke(jni::Env& env, jni::JObject classLoader, NativeKObject* instance, const std::vector<NativeTValue>& args);

private:
    jni::JObject wrapped;

    // local caches
    std::string name;
    std::string registrationName;
    int parameterCount = -1;
};


#endif //GODOT_LOADER_NATIVE_KFUNCTION_H

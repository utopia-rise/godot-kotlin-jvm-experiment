#ifndef GODOT_LOADER_NATIVE_CLASS_HANDLE_H
#define GODOT_LOADER_NATIVE_CLASS_HANDLE_H
#include <jvm.h>
#include "jni_utils.h"
#include "native_kobject.h"

class NativeClassHandle {
public:
    NativeClassHandle() = default;
    NativeClassHandle(const NativeClassHandle&) = delete;
    void operator=(const NativeClassHandle&) = delete;

    void init(jni::Env& env, jni::JObject jClassHandle);
    void registerClass(jni::Env& env, jni::JObject classLoader, void* nativescriptHandle);
    void dispose(jni::Env& env);

    NativeKObject* wrap(jni::Env& env, jni::JObject classLoader, void* ptr);

    static JClassHelper JH;
private:
    jni::JObject wrapped;

    // local caches
    std::string className;
    std::string superClass;

    const std::string& getClassName(jni::Env& env, jni::JObject classLoader);
    const std::string& getSuperClass(jni::Env& env, jni::JObject classLoader);
};


#endif //GODOT_LOADER_NATIVE_CLASS_HANDLE_H

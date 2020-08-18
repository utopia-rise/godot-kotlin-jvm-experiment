#ifndef GODOT_LOADER_NATIVE_KOBJECT_H
#define GODOT_LOADER_NATIVE_KOBJECT_H
#include <jvm.h>
#include "jni_utils.h"

class NativeKObject {
public:
    NativeKObject() = default;

    void init(jni::Env& env, jni::JObject object);
    void dispose(jni::Env& env);

    void _onInit(jni::Env& env, jni::JObject classLoader);
    void _onDestroy(jni::Env& env, jni::JObject classLoader);

    static JClassHelper JH;

private:
    jni::JObject wrapped;
};


#endif //GODOT_LOADER_NATIVE_KOBJECT_H

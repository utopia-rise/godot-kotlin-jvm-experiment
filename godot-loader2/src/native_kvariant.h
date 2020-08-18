#ifndef GODOT_LOADER_NATIVE_KVARIANT_H
#define GODOT_LOADER_NATIVE_KVARIANT_H
#include <jvm.h>
#include "jni_utils.h"

class NativeKVariant {
public:
    NativeKVariant(jni::JObject wrapped);
    void operator=(const NativeKVariant&) = delete;

    static JClassHelper JH;

private:
    jni::JObject wrapped;
};


#endif //GODOT_LOADER_NATIVE_KVARIANT_H

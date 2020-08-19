#ifndef GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H
#define GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H
#include <jvm.h>
#include "jni_utils.h"

class NativeTransferContext {
public:
    NativeTransferContext() = default;

    void init(jni::Env& env, jni::JObject object);
    void dispose(jni::Env& env);

    void* getBuffer(jni::Env& env, jni::JObject classLoader);
    int getBufferCapacity(jni::Env& env, jni::JObject classLoader);
    bool ensureCapacity(jni::Env& env, jni::JObject classLoader, int capacity);

    static JClassHelper JH;
private:
    jni::JObject wrapped;
};


#endif //GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H

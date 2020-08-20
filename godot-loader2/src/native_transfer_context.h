#ifndef GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H
#define GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H
#include <jvm.h>
#include "jni_utils.h"
#include "wire.pb.h"
#include "native_tvalue.h"

class NativeTransferContext {
public:
    NativeTransferContext() = default;

    void init(jni::Env& env, jni::JObject object);
    void dispose(jni::Env& env);

    void* getBuffer(jni::Env& env, jni::JObject classLoader);
    int getBufferCapacity(jni::Env& env, jni::JObject classLoader);
    bool ensureCapacity(jni::Env& env, jni::JObject classLoader, int capacity);

    static void writeReturnValue(void* buffer, int capacity, NativeTValue value);
    static NativeTValue readReturnValue(void* buffer, int capacity);

    static void writeArgs(void* buffer, int capacity, const std::vector<NativeTValue>& args);
    static std::vector<NativeTValue> readArgs(void* buffer, int capacity);

    static JClassHelper JH;
private:
    jni::JObject wrapped;
};


#endif //GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H

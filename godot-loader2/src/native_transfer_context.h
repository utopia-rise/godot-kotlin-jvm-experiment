#ifndef GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H
#define GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H
#include <jvm.h>
#include "jni_utils.h"
#include "wire.pb.h"
#include "native_tvalue.h"

class NativeTransferContext {
public:
    NativeTransferContext() = default;
    NativeTransferContext(const NativeTransferContext&) = delete;
    void operator=(const NativeTransferContext&) = delete;

    void init(jni::Env& env, jni::JObject& object);
    void dispose(jni::Env& env);

    void* getBuffer(jni::Env& env, jni::JObject& classLoader);
    int getBufferCapacity(jni::Env& env, jni::JObject& classLoader);
    bool ensureCapacity(jni::Env& env, jni::JObject& classLoader, int capacity);

    static void registerNatives(jni::Env& env, jni::JObject& classLoader);

    static void writeReturnValue(void* buffer, int capacity, const NativeTValue& value);
    static NativeTValue readReturnValue(void* buffer, int capacity);

    static void writeArgs(void* buffer, int capacity, const std::vector<NativeTValue>& args);
    static std::vector<NativeTValue> readArgs(void* buffer, int capacity);

    static JClassHelper JH;
private:
    jni::JObject wrapped;

    static void icall(JNIEnv* rawEnv, jobject instance, jlong jPtr, jstring jClassName, jstring jMethod, jint expectedReturnType);
};


#endif //GODOT_LOADER_NATIVE_TRANSFER_CONTEXT_H

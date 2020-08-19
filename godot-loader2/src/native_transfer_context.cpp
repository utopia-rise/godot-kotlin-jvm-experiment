#include <cassert>
#include "native_transfer_context.h"

JClassHelper NativeTransferContext::JH = JClassHelper("godot.wire.TransferContext");

void NativeTransferContext::init(jni::Env& env, jni::JObject object) {
    wrapped = object.newGlobalRef(env);
}

void NativeTransferContext::dispose(jni::Env& env) {
    wrapped.deleteGlobalRef(env);
}

void* NativeTransferContext::getBuffer(jni::Env& env, jni::JObject classLoader) {
    auto method = JH.getMethodId(env, classLoader, "getBuffer", "()Ljava/nio/ByteBuffer;");
    auto buffer = wrapped.callObjectMethod(env, method);
    assert(!buffer.isNull());
    return env.getDirectBufferAddress(buffer);
}

int NativeTransferContext::getBufferCapacity(jni::Env& env, jni::JObject classLoader) {
    auto method = JH.getMethodId(env, classLoader, "getBuffer", "()Ljava/nio/ByteBuffer;");
    auto buffer = wrapped.callObjectMethod(env, method);
    assert(!buffer.isNull());
    return env.getDirectBufferCapacity(buffer);
}

bool NativeTransferContext::ensureCapacity(jni::Env& env, jni::JObject classLoader, int capacity) {
    auto method = JH.getMethodId(env, classLoader, "ensureCapacity", "(I)Z");
    auto bufferChanged = wrapped.callBooleanMethod(env, method, {capacity});
    return bufferChanged == JNI_TRUE;
}

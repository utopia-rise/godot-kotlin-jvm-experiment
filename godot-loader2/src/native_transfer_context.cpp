#include <cassert>
#include <google/protobuf/util/delimited_message_util.h>
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

void NativeTransferContext::writeReturnValue(void* buffer, int capacity, NativeTValue value) {
    throw std::runtime_error("not implemented");
}

NativeTValue NativeTransferContext::readReturnValue(void* buffer, int capacity) {
    auto retValue = KReturnValue();
    google::protobuf::io::ArrayInputStream is(buffer, capacity);
    google::protobuf::io::CodedInputStream cis(&is);
    bool cleanEof;
    google::protobuf::util::ParseDelimitedFromCodedStream(&retValue, &cis, &cleanEof);
    return {retValue.data()};
}

void NativeTransferContext::writeArgs(void* buffer, int capacity, const std::vector<NativeTValue>& args) {
    auto funcArgs = KFuncArgs();
    for (const auto& arg : args) {
        funcArgs.add_args()->CopyFrom(arg.data);
    }
    google::protobuf::io::ArrayOutputStream os(buffer, capacity);
    google::protobuf::io::CodedOutputStream cos(&os);
    google::protobuf::util::SerializeDelimitedToCodedStream(funcArgs, &cos);
}

std::vector<NativeTValue> NativeTransferContext::readArgs(void* buffer, int capacity) {
    throw std::runtime_error("not implemented");
}

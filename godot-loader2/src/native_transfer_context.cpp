#include <cassert>
#include <google/protobuf/util/delimited_message_util.h>
#include "native_transfer_context.h"
#include "native_binding_context.h"
#include "icall_args.h"
#include "method_bind_cache.h"
#include "godot.h"

JClassHelper NativeTransferContext::JH = JClassHelper("godot.wire.TransferContext");

void NativeTransferContext::icall(JNIEnv* rawEnv, jobject instance, jlong jPtr, jstring jClassName, jstring jMethod,
                                  jint expectedReturnType) {
    auto& bindingContext = NativeBindingContext::instance();
    auto& transferContext = bindingContext.getTransferContext();
    auto& classLoader = bindingContext.getClassLoader();
    jni::Env env(rawEnv);
    auto buffer = transferContext.getBuffer(env, classLoader);
    auto bufferCapacity = transferContext.getBufferCapacity(env, classLoader);
    auto tArgs = NativeTransferContext::readArgs(buffer, bufferCapacity);
    auto icallArgs = ICallArgs();
    for (const auto& tArg : tArgs) {
        icallArgs.addArg(tArg.data);
    }
    auto& godot = Godot::instance();
    auto ptr = reinterpret_cast<godot_object*>(jPtr);
    auto className = env.fromJString(jni::JString(jClassName));
    auto method = env.fromJString(jni::JString(jMethod));
    auto mb = MethodBindCache::get(className.c_str(), method.c_str());
    auto retICallValue = ICallValue((KVariant::TypeCase) expectedReturnType);
    godot.gd->godot_method_bind_ptrcall(mb, ptr, (const void**) icallArgs.asRawData().data(), &retICallValue.data);
    auto retValue = retICallValue.toKVariant();
    if (transferContext.ensureCapacity(env, classLoader, retValue.ByteSizeLong())) {
        buffer = transferContext.getBuffer(env, classLoader);
        bufferCapacity = transferContext.getBufferCapacity(env, classLoader);
    }
    NativeTransferContext::writeReturnValue(buffer, bufferCapacity, NativeTValue(retValue));
}

void NativeTransferContext::registerNatives(jni::Env& env, jni::JObject& classLoader) {
    auto cls = JH.getClass(env, classLoader);
    jni::JNativeMethod icallMethod {
        "icall",
        "(JLjava/lang/String;Ljava/lang/String;I)V",
        (void*) &icall
    };
    auto methods = std::vector<jni::JNativeMethod>({icallMethod});
    cls.registerNatives(env, methods);
}

void NativeTransferContext::init(jni::Env& env, jni::JObject& object) {
    wrapped = object.newGlobalRef(env);
}

void NativeTransferContext::dispose(jni::Env& env) {
    wrapped.deleteGlobalRef(env);
}

void* NativeTransferContext::getBuffer(jni::Env& env, jni::JObject& classLoader) {
    auto method = JH.getMethodId(env, classLoader, "getBuffer", "()Ljava/nio/ByteBuffer;");
    auto buffer = wrapped.callObjectMethod(env, method);
    assert(!buffer.isNull());
    return env.getDirectBufferAddress(buffer);
}

int NativeTransferContext::getBufferCapacity(jni::Env& env, jni::JObject& classLoader) {
    auto method = JH.getMethodId(env, classLoader, "getBuffer", "()Ljava/nio/ByteBuffer;");
    auto buffer = wrapped.callObjectMethod(env, method);
    assert(!buffer.isNull());
    return env.getDirectBufferCapacity(buffer);
}

bool NativeTransferContext::ensureCapacity(jni::Env& env, jni::JObject& classLoader, int capacity) {
    auto method = JH.getMethodId(env, classLoader, "ensureCapacity", "(I)Z");
    auto bufferChanged = wrapped.callBooleanMethod(env, method, {capacity});
    return bufferChanged == JNI_TRUE;
}

void NativeTransferContext::writeReturnValue(void* buffer, int capacity, const NativeTValue& value) {
    auto retValue = KReturnValue();
    retValue.mutable_data()->CopyFrom(value.data);
    google::protobuf::io::ArrayOutputStream os(buffer, capacity);
    google::protobuf::io::CodedOutputStream cos(&os);
    google::protobuf::util::SerializeDelimitedToCodedStream(retValue, &cos);
}

NativeTValue NativeTransferContext::readReturnValue(void* buffer, int capacity) {
    auto retValue = KReturnValue();
    google::protobuf::io::ArrayInputStream is(buffer, capacity);
    google::protobuf::io::CodedInputStream cis(&is);
    bool cleanEof;
    google::protobuf::util::ParseDelimitedFromCodedStream(&retValue, &cis, &cleanEof);
    return NativeTValue(retValue.data());
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
    auto kArgs = KFuncArgs();
    google::protobuf::io::ArrayInputStream is(buffer, capacity);
    google::protobuf::io::CodedInputStream cis(&is);
    bool cleanEof;
    google::protobuf::util::ParseDelimitedFromCodedStream(&kArgs, &cis, &cleanEof);
    auto args = std::vector<NativeTValue>();
    for (const auto& kArg : kArgs.args()) {
        args.emplace_back(NativeTValue(kArg));
    }
    return args;
}

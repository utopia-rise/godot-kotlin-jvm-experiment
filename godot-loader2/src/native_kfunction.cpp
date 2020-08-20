#include <cassert>
#include "native_kfunction.h"
#include "native_binding_context.h"
#include <google/protobuf/io/zero_copy_stream.h>

JClassHelper NativeKFunction::JH = JClassHelper("godot.registry.KFunc");

void NativeKFunction::init(jni::Env& env, jni::JObject object) {
    wrapped = object.newGlobalRef(env);
}

void NativeKFunction::dispose(jni::Env& env) {
    wrapped.deleteGlobalRef(env);
}

const std::string& NativeKFunction::getName(jni::Env& env, jni::JObject classLoader) {
    if (!name.empty()) {
        return name;
    }
    auto method = JH.getMethodId(env, classLoader, "getName", "()Ljava/lang/String;");
    auto ret = wrapped.callObjectMethod(env, method);
    assert(!ret.isNull());
    name = env.fromJString(jni::JString((jstring) ret.obj));
    return name;
}

const std::string& NativeKFunction::getRegistrationName(jni::Env& env, jni::JObject classLoader) {
    if (!registrationName.empty()) {
        return registrationName;
    }
    auto method = JH.getMethodId(env, classLoader, "getRegistrationName", "()Ljava/lang/String;");
    auto ret = wrapped.callObjectMethod(env, method);
    assert(!ret.isNull());
    registrationName = env.fromJString(jni::JString((jstring) ret.obj));
    return registrationName;
}

int NativeKFunction::getParameterCount(jni::Env& env, jni::JObject classLoader) {
    if (parameterCount != -1) {
        return parameterCount;
    }
    auto method = JH.getMethodId(env, classLoader, "getParameterCount", "()I");
    parameterCount = wrapped.callIntMethod(env, method);
    return parameterCount;
}

NativeTValue NativeKFunction::invoke(jni::Env& env, jni::JObject classLoader, NativeKObject* instance, const std::vector<NativeTValue>& args) {
    auto& bindingContext = NativeBindingContext::instance();
    auto& transferContext = bindingContext.transferContext;
    auto invokeMethod = JH.getMethodId(
            env,
            classLoader,
            "invoke",
            "(Lgodot/internal/KObject;)Z"
    );
    auto buffer = transferContext.getBuffer(env, classLoader);
    auto bufferCapacity = transferContext.getBufferCapacity(env, classLoader);
    NativeTransferContext::writeArgs(buffer, bufferCapacity, args);
    auto bufferChanged = wrapped.callBooleanMethod(env, invokeMethod, {instance->wrapped});
    if (bufferChanged == JNI_TRUE) {
        buffer = transferContext.getBuffer(env, classLoader);
        bufferCapacity = transferContext.getBufferCapacity(env, classLoader);
    }
    return NativeTransferContext::readReturnValue(buffer, bufferCapacity);
}

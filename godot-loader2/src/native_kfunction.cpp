#include <cassert>
#include "native_kfunction.h"

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

NativeKVariant NativeKFunction::invoke(jni::Env& env, jni::JObject classLoader, NativeKObject* instance, std::vector<NativeKVariant> args) {
    auto cls = NativeKVariant::JH.getClass(env, classLoader);
    auto jvmArgs = cls.newObjectArray(env, 0);
    auto invokeMethod = JH.getMethodId(
            env,
            classLoader,
            "invoke",
            "(Lgodot/internal/KObject;[Lgodot/internal/KVariant;)Lgodot/internal/KVariant;"
    );
    wrapped.callObjectMethod(env, invokeMethod, {instance->wrapped, jvmArgs});

    return NativeKVariant(jni::JObject());
}

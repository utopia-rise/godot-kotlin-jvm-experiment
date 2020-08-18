#include <cassert>
#include "native_class_handle.h"
#include "godot.h"
#include "bridge.h"


void NativeClassHandle::init(jni::Env& env, jni::JObject jClassHandle) {
    // so it won't be gc'd by jvm
    wrapped = jClassHandle.newGlobalRef(env);
}

void NativeClassHandle::dispose(jni::Env& env) {
    // make it eligible for gc
    wrapped.deleteGlobalRef(env);
}

void NativeClassHandle::registerClass(jni::Env& env, jni::JObject classLoader, void* nativescriptHandle) {
    auto& godot = Godot::instance();
    // TODO: get from java
    auto isTool = false;
    auto className = getClassName(env, classLoader);
    auto superClass = getSuperClass(env, classLoader);

    auto ctor = godot_instance_create_func {&createInstance, this, &disposeClassHandle};
    auto dtor = godot_instance_destroy_func {&destroyInstance, this, nullptr};

    auto registerMethod = isTool ? godot.ns->godot_nativescript_register_tool_class
                                 : godot.ns->godot_nativescript_register_class;

    registerMethod(nativescriptHandle, className.c_str(), superClass.c_str(), ctor, dtor);
}

JClassHelper NativeClassHandle::JH = JClassHelper("godot.registry.ClassHandle");

const std::string& NativeClassHandle::getClassName(jni::Env& env, jni::JObject classLoader) {
    if (!className.empty()) {
        return className;
    }
    auto method = JH.getMethodId(env, classLoader, "getClassName", "()Ljava/lang/String;");
    auto ret = wrapped.callObjectMethod(env, method);
    assert(!ret.isNull());
    className = env.fromJString(jni::JString((jstring) ret.obj));
    return className;
}

const std::string& NativeClassHandle::getSuperClass(jni::Env& env, jni::JObject classLoader) {
    if (!superClass.empty()) {
        return superClass;
    }
    auto method = JH.getMethodId(env, classLoader, "getSuperClass", "()Ljava/lang/String;");
    auto ret = wrapped.callObjectMethod(env, method);
    assert(!ret.isNull());
    superClass = env.fromJString(jni::JString((jstring) ret.obj));
    return superClass;
}

NativeKObject* NativeClassHandle::wrap(jni::Env& env, jni::JObject classLoader, void* ptr) {
    auto wrapMethod = JH.getMethodId(env, classLoader, "wrap", "(J)Lgodot/internal/KObject;");
    auto obj = wrapped.callObjectMethod(env, wrapMethod, {reinterpret_cast<std::intptr_t>(ptr)});
    auto nativeKObject = new NativeKObject();
    nativeKObject->init(env, obj);
    return nativeKObject;
}

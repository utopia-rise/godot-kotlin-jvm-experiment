#include "native_kobject.h"

JClassHelper NativeKObject::JH = JClassHelper("godot.internal.KObject");

void NativeKObject::init(jni::Env& env, jni::JObject object) {
    // so it won't be gc'd by jvm
    wrapped = object.newGlobalRef(env);
}

void NativeKObject::dispose(jni::Env& env) {
    wrapped.deleteGlobalRef(env);
}

void NativeKObject::_onInit(jni::Env& env, jni::JObject classLoader) {

}

void NativeKObject::_onDestroy(jni::Env& env, jni::JObject classLoader) {

}

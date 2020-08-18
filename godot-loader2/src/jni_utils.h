#ifndef GODOT_LOADER_JNI_UTILS_H
#define GODOT_LOADER_JNI_UTILS_H
#include <jvm.h>

jni::JClass loadClass(jni::Env& env, jni::JObject classLoader, const char* name);

#endif //GODOT_LOADER_JNI_UTILS_H

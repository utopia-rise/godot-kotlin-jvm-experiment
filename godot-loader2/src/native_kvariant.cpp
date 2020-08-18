#include "native_kvariant.h"

JClassHelper NativeKVariant::JH = JClassHelper("godot.internal.KVariant");

NativeKVariant::NativeKVariant(jni::JObject wrapped) {
    this->wrapped = wrapped;
}

package jni

import jni.sys.JNI_FALSE
import jni.sys.JNI_TRUE
import jni.sys.jvalue

@OptIn(ExperimentalUnsignedTypes::class)
internal fun jvalue.setFrom(value: Any?) {
    when (value) {
        is JObject? -> l = value?.handle
        is Int -> i = value
        is Long -> j = value
        is Float -> f = value
        is Double -> d = value
        is UShort -> c = value
        is Byte -> b = value
        is Short -> s = value
        is Boolean -> z = if (value) {
            JNI_TRUE.toUByte()
        } else {
            JNI_FALSE.toUByte()
        }
        else -> throw JniError("$value can't be converted to a jvalue!")
    }
}
package jni

import jni.sys.*

enum class JNIVersion(internal val value: Int) {
    JNI_1_1(JNI_VERSION_1_1),
    JNI_1_2(JNI_VERSION_1_2),
    JNI_1_4(JNI_VERSION_1_4),
    JNI_1_6(JNI_VERSION_1_6),
    JNI_1_8(JNI_VERSION_1_8),
    JNI_9(JNI_VERSION_9),
    JNI_10(JNI_VERSION_10)
}
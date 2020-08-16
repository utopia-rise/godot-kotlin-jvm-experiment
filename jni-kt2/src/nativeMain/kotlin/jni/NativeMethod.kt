package jni

import kotlinx.cinterop.COpaquePointer

class NativeMethod(
    val name: String,
    val signature: String,
    val method: COpaquePointer
)
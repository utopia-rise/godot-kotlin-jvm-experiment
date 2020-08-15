package jni

import jni.sys.*
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed

class JNIError(message: String): Throwable(message)

fun checkError(code: Int, message: String) {
    if (code != JNI_OK) {
        throw JNIError(message)
    }
}

val JNIEnvVar.f: JNINativeInterface_
    get() = this.pointed!!

val JavaVMVar.f: JNIInvokeInterface_
    get() = this.pointed!!

fun <T : CPointer<CFunction<*>>> T?.s(): T {
    return this!!
}
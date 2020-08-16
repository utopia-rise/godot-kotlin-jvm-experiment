package jni

import jni.sys.JavaVMVar
import jni.sys.jint
import jni.sys.jsize
import jni.sys.jsizeVar
import kotlinx.cinterop.*
import kotlin.native.concurrent.ThreadLocal

typealias CreateJavaVM = CPointer<CFunction<(CValuesRef<CPointerVar<JavaVMVar>>?, CValuesRef<COpaquePointerVar>?, CValuesRef<*>?) -> jint>>
typealias GetCreatedJavaVMs = CPointer<CFunction<(CValuesRef<CPointerVar<JavaVMVar>>?, jsize, CValuesRef<jsizeVar>?) -> jint>>

@ThreadLocal
expect object JvmLoader {
    val jvmLib: COpaquePointer
    fun getCreateJavaVmFunctionFromEmbeddedJvm(): CreateJavaVM
    fun getGetCreatedJavaVMsFunctionFromEmbeddedJvm(): GetCreatedJavaVMs
}
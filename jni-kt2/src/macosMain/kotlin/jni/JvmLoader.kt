package jni

import kotlin.native.concurrent.ThreadLocal
import kotlinx.cinterop.*
import platform.posix.RTLD_NOW
import platform.posix.dlopen
import platform.posix.dlsym

@ThreadLocal
object JvmLoader {
    val jvmLib: COpaquePointer by lazy {
        val jvmLibPath = "jre/lib/server/libjvm.dylib"
        val jvmLib = dlopen(jvmLibPath, RTLD_NOW)
        requireNotNull(jvmLib) { "Could not load jre lib from path $jvmLibPath" }
    }

    fun getCreateJavaVmFunctionFromEmbeddedJvm(): CreateJavaVM {
        val createJavaVmFunctionPointer = dlsym(jvmLib, "JNI_CreateJavaVM")
        requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_CreateJavaVM function pointer" }
        return createJavaVmFunctionPointer.reinterpret()
    }

    fun getGetCreatedJavaVMsFunctionFromEmbeddedJvm(): GetCreatedJavaVMs {
        val createJavaVmFunctionPointer = dlsym(jvmLib, "JNI_GetCreatedJavaVMs")
        requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_GetCreatedJavaVMs function pointer" }
        return createJavaVmFunctionPointer.reinterpret()
    }
}
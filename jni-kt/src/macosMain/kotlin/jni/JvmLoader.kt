package jni

import kotlinx.cinterop.COpaquePointer
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
actual object JvmLoader {
    actual val jvmLib: COpaquePointer by lazy {
        TODO()
    }

    actual fun getCreateJavaVmFunctionFromEmbeddedJvm(): CreateJavaVM {
        val createJavaVmFunctionPointer = dlsym(jvmLib, "JNI_CreateJavaVM")
        requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_CreateJavaVM function pointer" }
        return createJavaVmFunctionPointer.reinterpret()
    }

    actual fun getGetCreatedJavaVMsFunctionFromEmbeddedJvm(): GetCreatedJavaVMs {
        val createJavaVmFunctionPointer = dlsym(jvmLib, "JNI_GetCreatedJavaVMs")
        requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_GetCreatedJavaVMs function pointer" }
        return createJavaVmFunctionPointer.reinterpret()
    }
}
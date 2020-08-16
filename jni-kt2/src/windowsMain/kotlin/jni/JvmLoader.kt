@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package jni

import kotlin.native.concurrent.ThreadLocal
import kotlinx.cinterop.*
import platform.posix.sleep
import platform.windows.*

@ThreadLocal
object JvmLoader {
    val jvmLib by lazy {
        val jvmLibPath = "jre/bin/server/jvm.dll"
        val jvmLib = loadLibrary(jvmLibPath)
        requireNotNull(jvmLib) { "Could not load jre lib from path $jvmLibPath" }
    }

    private fun loadLibrary(path: String): HMODULE? {
        sleep(5)
        memScoped {
            val pathAsWString = path.wcstr.getPointer(this)
            val jvmLib = LoadLibrary?.let { it(pathAsWString) }
            if (jvmLib == null) {
                val lastError = GetLastError()
                if (lastError == 126u) {
                    // "The specified module could not be found."
                    // load vcruntime140.dll from the bundled JRE, then try again
                    println("WARNING: could not load the bundled JRE. Trying to load vcruntime140.dll first and trying again...")

                    val msvrcPathAsWString = "jre/bin/vcruntime140.dll".wcstr.getPointer(this)
                    val hinstVCR = LoadLibrary?.let { it(msvrcPathAsWString) }
                    if (hinstVCR != null) {
                        return LoadLibrary?.let { it(pathAsWString) }
                    }
                }
            }
            return jvmLib
        }
    }

    fun getCreateJavaVmFunctionFromEmbeddedJvm(): CreateJavaVM {
        val createJavaVmFunctionPointer = GetProcAddress(jvmLib, "JNI_CreateJavaVM")
        requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_CreateJavaVM function pointer" }
        return createJavaVmFunctionPointer.reinterpret()
    }

    fun getGetCreatedJavaVMsFunctionFromEmbeddedJvm(): GetCreatedJavaVMs {
        val createJavaVmFunctionPointer = GetProcAddress(jvmLib, "JNI_GetCreatedJavaVMs")
        requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_GetCreatedJavaVMs function pointer" }
        return createJavaVmFunctionPointer.reinterpret()
    }
}
package jni

import jni.sys.*
import kotlinx.cinterop.*
import platform.posix.RTLD_NOW
import platform.posix.dlopen
import platform.posix.dlsym

typealias CreateJavaVM = CPointer<CFunction<(CValuesRef<CPointerVar<JavaVMVar>>?, CValuesRef<COpaquePointerVar>?, CValuesRef<*>?) -> jint>>
typealias GetCreatedJavaVMs = CPointer<CFunction<(CValuesRef<CPointerVar<JavaVMVar>>?, jsize, CValuesRef<jsizeVar>?) -> jint>>

class JavaVM(private val handle: JavaVMVar, val version: JNIVersion) {
    fun <T> attach(block: JNIEnv.() -> T): T {
        return attach().block().also { detach() }
    }

    fun getEnv(): JNIEnv {
        return memScoped {
            val env = allocPointerTo<JNIEnvVar>()
            val result = (handle.f.GetEnv!!)(
                handle.ptr,
                env.ptr.reinterpret(),
                version.value
            )
            checkError(result, "Unable to get JNIEnv, is current thread attached?")
            JNIEnv(env.pointed!!)
        }
    }

    fun attach(): JNIEnv {
        return memScoped {
            val env = allocPointerTo<JNIEnvVar>()
            val args = cValue<JavaVMAttachArgs>() {
                version = this@JavaVM.version.value
            }
            val result = (handle.f.AttachCurrentThread!!)(handle.ptr, env.ptr.reinterpret(), args.ptr)
            checkError(result, "Failed to attach to current thread!")
            JNIEnv(env.pointed!!)
        }
    }

    fun detach() {
        memScoped {
            val result = (handle.f.DetachCurrentThread!!)(handle.ptr)
            checkError(result, "Failed to detach from current thread!")
        }
    }

    fun destroy() {}

    companion object {
        private val jvmLib by lazy {
            //TODO: windows and macOS switch
            val jvmLibPath = "jre/lib/server/libjvm.so"
            val jvmLib = dlopen(jvmLibPath, RTLD_NOW)
            requireNotNull(jvmLib) { "Could not load jre lib from path $jvmLibPath" }
        }

        fun create(args: JavaVMInitArgs): JavaVM {
            return memScoped {
                val jvmArgs = cValue<jni.sys.JavaVMInitArgs> {
                    version = args.version.value
                    nOptions = args.options.size
                    options = allocArray<JavaVMOption>(nOptions).also { options ->
                        args.options.forEachIndexed { i, option ->
                            options[i].optionString = option.str.cstr.ptr
                        }
                    }
                }

                val vm = allocPointerTo<JavaVMVar>()
                val env = allocPointerTo<JNIEnvVar>()
                val result = getCreateJavaVmFunctionFromEmbeddedJvm().invoke(vm.ptr, env.ptr.reinterpret(), jvmArgs.ptr)
                checkError(result, "Failed to create Java Virtual Machine! err_code=$result")
                JavaVM(vm.pointed!!, args.version).also { it.detach() }
            }
        }

        fun getExisting(version: JNIVersion): JavaVM? {
            return memScoped {
                val buffer = allocPointerTo<JavaVMVar>().ptr
                val vmCounts = alloc<IntVar>()
                getGetCreatedJavaVMsFunctionFromEmbeddedJvm().invoke(buffer, 1, vmCounts.ptr)
                if (vmCounts.value > 0) {
                    val vm = buffer.pointed.value!!
                    JavaVM(vm.pointed, version).also { it.detach() }
                } else {
                    null
                }
            }
        }

        private fun getCreateJavaVmFunctionFromEmbeddedJvm(): CreateJavaVM {
            val createJavaVmFunctionPointer = dlsym(jvmLib, "JNI_CreateJavaVM")
            requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_CreateJavaVM function pointer" }
            return createJavaVmFunctionPointer.reinterpret()
        }

        private fun getGetCreatedJavaVMsFunctionFromEmbeddedJvm(): GetCreatedJavaVMs {
            val createJavaVmFunctionPointer = dlsym(jvmLib, "JNI_GetCreatedJavaVMs")
            requireNotNull(createJavaVmFunctionPointer) { "Could not get JNI_GetCreatedJavaVMs function pointer" }
            return createJavaVmFunctionPointer.reinterpret()
        }
    }
}
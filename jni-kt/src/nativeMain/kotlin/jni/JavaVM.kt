package jni

import jni.sys.*
import kotlinx.cinterop.*

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
                val result = JNI_CreateJavaVM(vm.ptr, env.ptr.reinterpret(), jvmArgs.ptr)
                checkError(result, "Failed to create Java Virtual Machine! err_code=$result")
                JavaVM(vm.pointed!!, args.version).also { it.detach() }
            }
        }

        fun getExisting(version: JNIVersion): JavaVM? {
            return memScoped {
                val buffer = allocPointerTo<JavaVMVar>().ptr
                val vmCounts = alloc<IntVar>()
                JNI_GetCreatedJavaVMs(buffer, 1, vmCounts.ptr)
                if (vmCounts.value > 0) {
                    val vm = buffer.pointed.value!!
                    JavaVM(vm.pointed, version).also { it.detach() }
                } else {
                    null
                }
            }
        }
    }
}
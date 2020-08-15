package godot.internal

import jni.*
import kotlinx.cinterop.*

class Jvm private constructor(private val jvm: JavaVM,
                              private val env: JNIEnv) {
    fun loadBinding() {

    }

    fun unloadBinding() {

    }

    fun destroy() {
        memScoped {
            val vm = alloc<JavaVMVar> {
                value = jvm
            }
            nullSafe(jvm.pointed.DestroyJavaVM)(vm.ptr)
        }
    }


    companion object {
        fun maybeCreate(): Jvm {
            return memScoped {
                val existingJavaVM = getExistingJavaVM()
                val env = allocPointerTo<JNIEnvVar>().ptr

                if (existingJavaVM != null) {
                    println("Re-using existing jvm ...")
                    val vm = alloc<JavaVMVar> {
                        value = existingJavaVM
                    }
                    nullSafe(existingJavaVM.pointed.GetEnv)(vm.ptr, env.reinterpret(), JNI_VERSION_1_8)
                    Jvm(existingJavaVM, nullSafe(env.pointed.value!!.pointed.value))
                } else {
                    val args = cValue<JavaVMInitArgs> {
                        version = JNI_VERSION_1_8
                        val jvmOptions = listOf(
                            "-Xcheck:jni"
                            //"-verbose:jni"
                        )
                        nOptions = jvmOptions.size
                        options = allocArray<JavaVMOption>(nOptions).also { options ->
                            jvmOptions.forEachIndexed { i, option ->
                                options[i].optionString = option.cstr.ptr
                            }
                        }
                    }
                    val vm = allocPointerTo<JavaVMVar>().ptr
                    val result = JNI_CreateJavaVM(vm, env.reinterpret(), args.ptr)
                    require(result == JNI_OK) { "Failed to create Java Virtual Machine! err=$result" }
                    Jvm(nullSafe(vm.pointed.value!!.pointed.value), nullSafe(env.pointed.value!!.pointed.value))
                }
            }
        }

        private fun getExistingJavaVM(): JavaVM? {
            return memScoped {
                val buffer = allocPointerTo<JavaVMVar>().ptr
                val vmCounts = alloc<IntVar>()
                JNI_GetCreatedJavaVMs(buffer, 1, vmCounts.ptr)
                if (vmCounts.value > 0) {
                    return buffer.pointed.value!!.pointed.value
                } else {
                    null
                }
            }

        }
    }
}
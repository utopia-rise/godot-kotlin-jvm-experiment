package jni

import jni.sys.*
import kotlinx.cinterop.*

@ThreadLocal
object JavaVm {
    private lateinit var handle: JavaVMVar
    private lateinit var version: JniVersion

    fun init(args: JavaVmInitArgs) {
        handle = getExisting() ?: create(args)
        version = args.version
        // begin in a detach state
        detach()
    }

    fun destroy() {

    }

    fun <T> attach(block: JniEnv.() -> T): T {
        val env = attach()
        JObject.env = env
        val result = block(env)
        detach()
        return result
    }

    fun attach(): JniEnv {
        return memScoped {
            val env = allocPointerTo<JNIEnvVar>()
            val args = cValue<JavaVMAttachArgs>() {
                version = this@JavaVm.version.value
            }
            val result = handle[InvokeFn::AttachCurrentThread](handle.ptr, env.ptr.reinterpret(), args.ptr)
            if (result != JNI_OK) {
                throw JniError("Failed to attach jvm to current thread!")
            }
            JniEnv(env.pointed!!)
        }
    }

    fun detach() {
        memScoped {
            val result = handle[InvokeFn::DetachCurrentThread](handle.ptr)
            if (result != JNI_OK) {
                throw JniError("Failed to detach jvm from current thread!")
            }
        }
    }


    private fun create(args: JavaVmInitArgs): JavaVMVar {
        return memScoped {
            val jvmArgs = cValue<JavaVMInitArgs> {
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
            if (result != JNI_OK)  {
                throw JniError("Failed to start the jvm!")
            }
            vm.pointed!!
        }
    }

    private fun getExisting(): JavaVMVar? {
        return memScoped {
            val buffer = allocPointerTo<JavaVMVar>().ptr
            val vmCounts = alloc<IntVar>()
            JNI_GetCreatedJavaVMs(buffer, 1, vmCounts.ptr)
            if (vmCounts.value > 0) {
                buffer.pointed.value!!.pointed
            } else {
                null
            }
        }
    }
}
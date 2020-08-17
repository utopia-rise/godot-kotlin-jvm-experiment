package jni

import jni.sys.jint
import jni.sys.jobject
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

open class JObject(internal val handle: jobject) {
    fun callObjectMethod(method: JMethodId, vararg args: Any?): JObject? {
        return memScoped {
            val result = env.handle[EnvFn::CallObjectMethodA](
                env.handle.ptr,
                handle,
                method.handle,
                convertToJValueArgs(args)
            )
            env.verifyNoErrors()
            result?.let { JObject(it) }
        }
    }

    fun callIntMethod(method: JMethodId, vararg args: Any?): Int {
        return memScoped {
            val result = env.handle[EnvFn::CallIntMethodA](
                env.handle.ptr,
                handle,
                method.handle,
                convertToJValueArgs(args)
            )
            env.verifyNoErrors()
            result
        }
    }

    fun callLongMethod(method: JMethodId, vararg args: Any?): Long {
        return memScoped {
            val result = env.handle[EnvFn::CallLongMethodA](
                env.handle.ptr,
                handle,
                method.handle,
                convertToJValueArgs(args)
            )
            env.verifyNoErrors()
            result
        }
    }

    fun callVoidMethod(method: JMethodId, vararg args: Any?) {
        memScoped {
            env.handle[EnvFn::CallVoidMethodA](
                env.handle.ptr,
                handle,
                method.handle,
                convertToJValueArgs(args)
            )
            env.verifyNoErrors()
        }
    }

    open fun newLocalRef(): JObject {
        return memScoped {
            val ref = env.handle[EnvFn::NewLocalRef](env.handle.ptr, handle)
            requireNotNull(ref) { "Failed to create local ref!" }
            JObject(ref)
        }
    }

    open fun newGlobalRef(): JObject {
        return memScoped {
            val ref = env.handle[EnvFn::NewGlobalRef](env.handle.ptr, handle)
            requireNotNull(ref) { "Failed to create global ref!" }
            JObject(ref)
        }
    }

    fun deleteLocalRef() {
        memScoped {
            env.handle[EnvFn::DeleteLocalRef](env.handle.ptr, handle)
        }
    }

    fun deleteGlobalRef() {
        memScoped {
            env.handle[EnvFn::DeleteGlobalRef](env.handle.ptr, handle)
        }
    }

    companion object {
        val env: JniEnv
            get() = JniEnv.current()
    }
}
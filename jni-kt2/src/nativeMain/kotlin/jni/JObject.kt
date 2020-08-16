package jni

import jni.sys.jobject
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

open class JObject(val env: JniEnv, internal val handle: jobject) {
    fun callObjectMethod(method: JMethodId, vararg args: Any?): JObject? {
        return memScoped {
            val result = env.handle[EnvFn::CallObjectMethodA](
                env.handle.ptr,
                handle,
                method.handle,
                convertToJValueArgs(args)
            )
            env.verifyNoErrors()
            result?.let { JObject(env, it) }
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

    fun toJString() = JString(env, handle)
}
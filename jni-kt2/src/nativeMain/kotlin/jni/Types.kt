package jni

import jni.sys.jarray
import jni.sys.jfieldID
import jni.sys.jmethodID
import jni.sys.jobject
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

class JMethodId(internal val handle: jmethodID)
class JFieldId(internal val handle: jfieldID)

class JString(env: JniEnv, handle: jobject) : JObject(env, handle)

abstract class JArray<T>(env: JniEnv, handle: jarray) : JObject(env, handle) {
    abstract operator fun get(index: Int): T
    abstract operator fun set(index: Int, value: T)
}

class JObjectArray(env: JniEnv, handle: jarray) : JArray<JObject?>(env, handle) {
    override fun get(index: Int): JObject? {
        return memScoped {
            val value = env.handle[EnvFn::GetObjectArrayElement](
                env.handle.ptr,
                handle,
                index
            )
            value?.let { JObject(env, it) }
        }
    }

    override fun set(index: Int, value: JObject?) {
        memScoped {
            env.handle[EnvFn::SetObjectArrayElement](
                env.handle.ptr,
                handle,
                index,
                value?.handle
            )
        }
    }
}
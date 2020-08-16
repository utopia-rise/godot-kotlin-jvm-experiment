package jni

import jni.sys.*
import kotlinx.cinterop.*

class JMethodId(internal val handle: jmethodID)
class JFieldId(internal val handle: jfieldID)

class JString(handle: jobject) : JObject(handle) {
    fun toKString(): String {
        return memScoped {
            val chars = env.handle[EnvFn::GetStringUTFChars](
                env.handle.ptr,
                handle,
                alloc<UByteVar>().ptr
            )
            checkNotNull(chars) { "Faild to convert java string to native string!" }
            chars.toKString().also {
                // clean up
                env.handle[EnvFn::ReleaseStringUTFChars](
                    env.handle.ptr,
                    handle,
                    chars
                )
            }
        }
    }
}

abstract class JArray<T>(handle: jarray) : JObject(handle) {
    abstract operator fun get(index: Int): T
    abstract operator fun set(index: Int, value: T)
    fun length(): Int {
        return memScoped {
            env.handle[EnvFn::GetArrayLength](
                env.handle.ptr,
                handle
            )
        }
    }
}

class JObjectArray(handle: jarray) : JArray<JObject?>(handle) {
    override fun get(index: Int): JObject? {
        return memScoped {
            val value = env.handle[EnvFn::GetObjectArrayElement](
                env.handle.ptr,
                handle,
                index
            )
            value?.let { JObject(it) }
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
package jni

import jni.sys.JNIEnvVar
import jni.sys.JNI_TRUE
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

class JniEnv(internal val handle: JNIEnvVar) {
    init {
        threadLocalEnv = this
    }

    fun findClassOrNull(className: String): JClass? {
        return memScoped {
            val jclass = handle[EnvFn::FindClass](handle.ptr, className.cstr.ptr)
            jclass?.let { JClass(it, className) }
        }
    }

    fun findClass(className: String): JClass {
        return requireNotNull(findClassOrNull(className)) { "Failed to find class $className" }
    }

    fun newString(str: String): JString {
        return memScoped {
            val jstring = handle[EnvFn::NewStringUTF](handle.ptr, str.cstr.ptr)
            checkNotNull(jstring) { "Failed to create java.lang.String for $str" }
            JString(jstring)
        }
    }

    fun newInstance(className: String, signature: String, vararg args: Any?): JObject {
        val cls = findClass(className)
        val ctor = cls.getConstructorMethodID(signature)
        return cls.newInstance(ctor, *args)
    }

    fun newObjectArray(className: String, size: Int, initial: JObject? = null): JObjectArray {
        val cls = findClass(className)
        return cls.newObjectArray(size, initial)
    }

    fun pushLocalFrame(initialCapacity: Int) {
        handle[EnvFn::PushLocalFrame](handle.ptr, initialCapacity)
    }

    fun popLocalFrame() {
        handle[EnvFn::PopLocalFrame](handle.ptr, null)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun exceptionOccurred(): Boolean {
        val result = handle[EnvFn::ExceptionCheck](handle.ptr)
        return result.toInt() == JNI_TRUE
    }

    private fun exceptionClear() {
        handle[EnvFn::ExceptionClear](handle.ptr)
    }

    private fun exceptionDescribe() {
        handle[EnvFn::ExceptionDescribe](handle.ptr)
    }

    internal fun verifyNoErrors() {
        if (exceptionOccurred()) {
            exceptionDescribe()
            exceptionClear()
            throw JniError("An exception has occurred!")
        }
    }

    @ThreadLocal
    companion object {
        private lateinit var threadLocalEnv: JniEnv

        fun current() = threadLocalEnv
    }
}
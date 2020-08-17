package jni

import jni.sys.JNINativeMethod
import jni.sys.JNI_OK
import jni.sys.jobject
import kotlinx.cinterop.*

class JClass(handle: jobject, val className: String) : JObject(handle) {
    fun getMethodIDOrNull(methodName: String, signature: String): JMethodId? {
        return memScoped {
            val methodID = env.handle[EnvFn::GetMethodID](
                env.handle.ptr,
                handle,
                methodName.cstr.ptr,
                signature.cstr.ptr
            )

            methodID?.let(::JMethodId)
        }
    }

    fun getMethodId(methodName: String, signature: String): JMethodId {
        return checkNotNull(getMethodIDOrNull(methodName, signature)) {
            "Failed to find method $methodName with signature $signature in class: $className"
        }
    }

    fun getStaticMethodIdOrNull(name: String, signature: String): JMethodId? {
        return memScoped {
            val methodID = env.handle[EnvFn::GetStaticMethodID](
                env.handle.ptr,
                handle,
                name.cstr.ptr,
                signature.cstr.ptr
            )

            methodID?.let(::JMethodId)
        }
    }

    fun getStaticMethodId(methodName: String, signature: String): JMethodId {
        return checkNotNull(getStaticMethodIdOrNull(methodName, signature)) {
            "Failed to get static method $methodName with signature $signature in class $className"
        }
    }

    fun getConstructorMethodIdOrNull(signature: String): JMethodId? {
        return getMethodIDOrNull("<init>", signature)
    }

    fun getConstructorMethodID(signature: String): JMethodId {
        return checkNotNull(getConstructorMethodIdOrNull(signature)) {
            "Failed to get constructor with signature $signature in class $className"
        }
    }

    fun getNoArgConstructorOrNull(): JMethodId? {
        return getConstructorMethodIdOrNull("()V")
    }

    fun getNoArgConstructor(): JMethodId {
        return checkNotNull(getNoArgConstructorOrNull()) {
            "Failed to get no-arg constructor of class $className"
        }
    }

    fun callStaticObjectMethod(method: JMethodId, vararg args: Any?): JObject? {
        return memScoped {
            val results = env.handle[EnvFn::CallStaticObjectMethodA](
                env.handle.ptr,
                handle,
                method.handle,
                convertToJValueArgs(args)
            )
            env.verifyNoErrors()
            results?.let { JObject(it) }
        }
    }


    fun getStaticFieldIdOrNull(name: String, signature: String): JFieldId? {
        return memScoped {
            val fieldID = env.handle[EnvFn::GetStaticFieldID](
                env.handle.ptr,
                handle,
                name.cstr.ptr,
                signature.cstr.ptr
            )
            fieldID?.let(::JFieldId)
        }
    }

    fun newInstance(constructor: JMethodId, vararg args: Any?): JObject {
        return memScoped {
            val instance = env.handle[EnvFn::NewObjectA](
                env.handle.ptr,
                handle,
                constructor.handle,
                convertToJValueArgs(args)
            )
            checkNotNull(instance) { "Failed to create instance for $handle for $className" }
            JObject(instance)
        }
    }

    fun newInstance(vararg args: Any?): JObject {
        return memScoped {
            val instance = env.handle[EnvFn::NewObjectA](
                env.handle.ptr,
                handle,
                getNoArgConstructor().handle,
                convertToJValueArgs(args)
            )
            checkNotNull(instance) { "Failed to create instance for $handle for $className" }
            JObject(instance)
        }
    }

    fun newObjectArray(size: Int, initial: JObject? = null): JObjectArray {
        return memScoped {
            val result = env.handle[EnvFn::NewObjectArray](
                env.handle.ptr,
                size,
                handle,
                initial?.handle
            )
            checkNotNull(result) { "Failed to create new object array for class $className!" }
            JObjectArray(result)
        }
    }

    fun registerNatives(methods: List<NativeMethod>) {
        memScoped {
            val nativeMethods = allocArray<JNINativeMethod>(methods.size)
            methods.forEachIndexed { i, v ->
                with(nativeMethods[i]) {
                    name = v.name.cstr.ptr
                    signature = v.signature.cstr.ptr
                    fnPtr = v.method
                }
            }
            val result = env.handle[EnvFn::RegisterNatives](
                env.handle.ptr,
                handle,
                nativeMethods,
                methods.size
            )
            env.verifyNoErrors()
            if (result != JNI_OK) {
                throw JniError("Failed to register native methods for class $className")
            }
        }
    }

    override fun newGlobalRef(): JClass {
        val ref = super.newGlobalRef()
        return JClass(ref.handle, className)
    }
}
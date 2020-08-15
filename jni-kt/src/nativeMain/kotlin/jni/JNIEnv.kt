package jni

import jni.sys.JNIEnvVar
import jni.sys.jvalue
import kotlinx.cinterop.*

class JNIEnv(private val handle: JNIEnvVar) {
    fun findClass(cls: String): JClass? {
        return memScoped {
            val jclass = (handle.f.FindClass!!)(handle.ptr, cls.cstr.ptr)
            jclass?.let(::JClass)
        }
    }

    fun newStringUTF(str: String): JString {
        return memScoped {
            val jstring = (handle.f.NewStringUTF!!)(handle.ptr, str.cstr.ptr)
            requireNotNull(jstring) { "Failed to create java.lang.String for $str" }
            JString(jstring)
        }
    }

    fun JMethodID.callObjectMethod(instance: JObject, vararg args: JValue<*>): JObject? {
        return memScoped {
            val a = allocArray<jvalue>(args.size)
            args.forEachIndexed { i, v ->
                v.set(a[i])
            }
            val result = (this@JNIEnv.handle.f.CallObjectMethodA!!)(
                this@JNIEnv.handle.ptr,
                instance.handle,
                handle,
                a
            )
            result?.let(::JObject)
        }
    }


    fun JClass.getStaticFieldId(name: String, signature: String): JFieldID? {
        return memScoped {
            val fieldID = (this@JNIEnv.handle.f.GetStaticFieldID!!)(
                this@JNIEnv.handle.ptr,
                handle,
                name.cstr.ptr,
                signature.cstr.ptr
            )
            fieldID?.let(::JFieldID)
        }
    }

    fun JClass.getMethodID(name: String, signature: String): JMethodID? {
        return memScoped {
            val methodID = (this@JNIEnv.handle.f.GetMethodID!!)(
                this@JNIEnv.handle.ptr,
                handle,
                name.cstr.ptr,
                signature.cstr.ptr
            )

            methodID?.let(::JMethodID)
        }
    }

    fun JClass.getConstructorMethodID(signature: String): JMethodID? {
        return getMethodID("<init>", signature)
    }

    fun JClass.newInstance(ctor: JMethodID, vararg args: JValue<*>): JObject {
        return memScoped {
            val a = allocArray<jvalue>(args.size)
            args.forEachIndexed { i, v ->
                v.set(a[i])
            }
            val instance = (this@JNIEnv.handle.f.NewObjectA!!)(
                this@JNIEnv.handle.ptr,
                handle,
                ctor.handle,
                a
            )
            requireNotNull(instance) { "Failed to create instance for $handle" }
            JObject(instance)
        }
    }

    fun JObject.newLocalRef(): JObject {
        return memScoped {
            val ref = (this@JNIEnv.handle.f.NewLocalRef!!)(this@JNIEnv.handle.ptr, this@newLocalRef.handle)
            requireNotNull(ref) { "Failed to create local ref!" }
            JObject(ref)
        }
    }

    fun JObject.deleteLocalRef() {
        memScoped {
            (this@JNIEnv.handle.f.DeleteLocalRef!!)(this@JNIEnv.handle.ptr, this@deleteLocalRef.handle)
        }
    }
}
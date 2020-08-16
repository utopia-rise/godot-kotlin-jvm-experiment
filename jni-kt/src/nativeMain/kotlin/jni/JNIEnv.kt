package jni

import jni.sys.JNIEnvVar
import jni.sys.jvalue
import kotlinx.cinterop.*

class JNIEnv(private val handle: JNIEnvVar) {
    fun findClassOrNull(cls: String): JClass? {
        return memScoped {
            val jclass = (handle.f.FindClass!!)(handle.ptr, cls.cstr.ptr)
            jclass?.let(::JClass)
        }
    }

    fun findClass(cls: String): JClass {
        return requireNotNull(findClassOrNull(cls)) { "Failed to find class $cls" }
    }

    fun loadClassOrNull(classLoader: JObject, cls: String): JObject? {
        return memScoped {
            val jclass = findClass("java/net/URLClassLoader")
            val findClassMethodID = jclass.getMethodID("loadClass", "(Ljava/lang/String;)Ljava/lang/Class;")
            findClassMethodID.callObjectMethod(classLoader, JValue.Object(newStringUTF(cls).handle))
        }
    }

    fun loadClass(classLoader: JObject, cls: String): JObject {
        return requireNotNull(loadClassOrNull(classLoader, cls)) { "Failed to load class $cls" }
    }

    fun newStringUTF(str: String): JString {
        return memScoped {
            val jstring = (handle.f.NewStringUTF!!)(handle.ptr, str.cstr.ptr)
            requireNotNull(jstring) { "Failed to create java.lang.String for $str" }
            JString(jstring)
        }
    }

    fun setObjectArrayElement(array: JArray, index: Int, value: JObject) {
        memScoped {
            (this@JNIEnv.handle.f.SetObjectArrayElement!!)(
                handle.ptr,
                array.handle,
                index,
                value.handle
            )
        }
    }

    fun JClass.newObjectArray(size: Int, initial: JObject? = null): JArray {
        return memScoped {
            val result = (this@JNIEnv.handle.f.NewObjectArray!!)(
                this@JNIEnv.handle.ptr,
                size,
                handle,
                initial?.handle
            )
            requireNotNull(result) { "Failed to create new object array!" }
            JArray(result)
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

    fun JMethodID.callVoidMethod(instance: JObject, vararg args: JValue<*>) {
        memScoped {
            val a = allocArray<jvalue>(args.size)
            args.forEachIndexed { i, v ->
                v.set(a[i])
            }
            (this@JNIEnv.handle.f.CallVoidMethodA!!)(
                this@JNIEnv.handle.ptr,
                instance.handle,
                handle,
                a
            )
        }
    }

    fun JClass.callStaticObjectMethod(method: JMethodID, vararg args: JValue<*>): JObject? {
        return memScoped {
            val a = allocArray<jvalue>(args.size)
            args.forEachIndexed { i, v ->
                v.set(a[i])
            }
            val results = (this@JNIEnv.handle.f.CallStaticObjectMethodA!!)(
                this@JNIEnv.handle.ptr,
                handle,
                method.handle,
                a
            )
            results?.let(::JObject)
        }
    }


    fun JClass.getStaticFieldIDOrNull(name: String, signature: String): JFieldID? {
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

    fun JClass.getMethodIDOrNull(name: String, signature: String): JMethodID? {
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

    fun JClass.getMethodID(name: String, signature: String): JMethodID {
        return requireNotNull(getMethodIDOrNull(name, signature)) {
            "Failed to find method $name with signature $signature"
        }
    }

    fun JClass.getStaticMethodIDOrNull(name: String, signature: String): JMethodID? {
        return memScoped {
            val methodID = (this@JNIEnv.handle.f.GetStaticMethodID!!)(
                this@JNIEnv.handle.ptr,
                handle,
                name.cstr.ptr,
                signature.cstr.ptr
            )

            methodID?.let(::JMethodID)
        }
    }

    fun JClass.getStaticMethodID(name: String, signature: String): JMethodID {
        return requireNotNull(getStaticMethodIDOrNull(name, signature)) {
            "Failed to find static method $name with signature $signature"
        }
    }

    fun JClass.getConstructorMethodIDOrNull(signature: String): JMethodID? {
        return getMethodIDOrNull("<init>", signature)
    }

    fun JClass.getConstructorMethodID(signature: String): JMethodID {
        return requireNotNull(getConstructorMethodIDOrNull(signature)) {
            "Failed to constructor with signature $signature in class: $this"
        }
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

    fun JObject.newGlobalRef(): JObject {
        return memScoped {
            val ref = (this@JNIEnv.handle.f.NewGlobalRef!!)(this@JNIEnv.handle.ptr, this@newGlobalRef.handle)
            requireNotNull(ref) { "Failed to create global ref!" }
            JObject(ref)
        }
    }

    fun JObject.deleteLocalRef() {
        memScoped {
            (this@JNIEnv.handle.f.DeleteLocalRef!!)(this@JNIEnv.handle.ptr, this@deleteLocalRef.handle)
        }
    }
}
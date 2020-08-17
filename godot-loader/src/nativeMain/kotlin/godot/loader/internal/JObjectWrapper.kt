package godot.loader.internal

import jni.JClass
import jni.JMethodId
import jni.extras.ClassLoader

abstract class JObjectWrapper(private val binaryName: String) {
    private var methodIdCache = mutableMapOf<String, JMethodId>()
    private var staticMethodIdCache = mutableMapOf<String, JMethodId>()

    val SGN = binaryName.replace(".", "/")
    lateinit var jclass: JClass

    fun init(classLoader: ClassLoader) {
        jclass = classLoader.loadClass(binaryName).newGlobalRef()
    }

    fun getMethodId(methodName: String, signature: String): JMethodId {
        val key = methodName + signature
        return methodIdCache.getOrPut(key) {
            jclass.getMethodId(methodName, signature)
        }
    }

    fun getStaticMethodId(methodName: String, signature: String): JMethodId {
        val key = methodName + signature
        return staticMethodIdCache.getOrPut(key) {
            jclass.getStaticMethodId(methodName, signature)
        }
    }

    fun teardown() {
        jclass.deleteGlobalRef()
    }
}
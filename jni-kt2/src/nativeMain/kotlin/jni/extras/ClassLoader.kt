package jni.extras

import jni.JClass
import jni.JObject
import jni.JniEnv
import jni.sys.jobject
import kotlinx.cinterop.memScoped


fun JniEnv.newClassLoader(classPath: List<String>): ClassLoader {
    val urls = URL.jclass(this).newObjectArray(classPath.size)
    classPath.forEachIndexed { i, v ->
        urls[i] = newFile(v).toURL()
    }
    return ClassLoader(
        this,
        newInstance(
            "java/net/URLClassLoader",
            "([Ljava/net/URL;)V",
            urls
        ).handle
    )
}

class ClassLoader(env: JniEnv, handle: jobject) : JObject(env, handle) {
    fun loadClassOrNull(className: String): JClass? {
        return memScoped {
            val urlClassLoaderClass = env.findClass("java/net/URLClassLoader")
            val findClassMethodID = urlClassLoaderClass.getMethodID("loadClass", "(Ljava/lang/String;)Ljava/lang/Class;")
            callObjectMethod(findClassMethodID, env.newString(className))?.let { JClass(env, it.handle, className) }
        }
    }

    fun loadClass(className: String): JClass {
        return requireNotNull(loadClassOrNull(className)) { "Failed to load class $className" }
    }
}
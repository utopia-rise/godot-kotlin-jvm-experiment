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
        newInstance(
            "java/net/URLClassLoader",
            "([Ljava/net/URL;)V",
            urls
        ).handle
    )
}

class ClassLoader(handle: jobject) : JObject(handle) {
    fun loadClassOrNull(className: String): JClass? {
        return memScoped {
            val urlClassLoaderClass = env.findClass("java/net/URLClassLoader")
            val findClassMethodID = urlClassLoaderClass.getMethodID("loadClass", "(Ljava/lang/String;)Ljava/lang/Class;")
            callObjectMethod(findClassMethodID, env.newString(className))?.let { JClass(it.handle, className) }
        }
    }

    fun loadClass(className: String): JClass {
        return requireNotNull(loadClassOrNull(className)) { "Failed to load class $className" }
    }

    override fun newLocalRef(): ClassLoader {
        return ClassLoader(super.newLocalRef().handle)
    }

    override fun newGlobalRef(): ClassLoader {
        return ClassLoader(super.newGlobalRef().handle)
    }
}
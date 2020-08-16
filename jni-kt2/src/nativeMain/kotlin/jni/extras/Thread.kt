package jni.extras

import jni.JClass
import jni.JObject
import jni.JniEnv
import jni.sys.jobject

fun JniEnv.currentThread(): Thread {
    val threadClass = Thread.jclass(this)
    val currentThreadMethodID = threadClass.getStaticMethodID("currentThread", "()Ljava/lang/Thread;")
    val currentThread = threadClass.callStaticObjectMethod(currentThreadMethodID)
    checkNotNull(currentThread) { "Failed to fetch current thread!" }
    return Thread(currentThread.handle)
}

class Thread(handle: jobject) : JObject(handle) {
    fun setContextClassLoader(classLoader: ClassLoader) {
        val threadClass = jclass(env)
        val setContextClassLoaderMethodId = threadClass.getMethodID("setContextClassLoader", "(Ljava/lang/ClassLoader;)V")
        callObjectMethod(setContextClassLoaderMethodId, classLoader)
    }

    fun getContextClassLoader(): ClassLoader? {
        val threadClass = jclass(env)
        val setContextClassLoaderMethodId = threadClass.getMethodID("getContextClassLoader", "()Ljava/lang/ClassLoader;")
        return callObjectMethod(setContextClassLoaderMethodId)?.let { ClassLoader(it.handle) }
    }

    fun loadClassOrNull(className: String): JClass? {
        // class loader uses `.` as separator instead of `/`
        return getContextClassLoader()?.loadClassOrNull(className.replace("/", "."))
    }

    fun loadClass(className: String): JClass {
        return checkNotNull(loadClassOrNull(className)) { "Failed to find class $className in current thread!" }
    }

    companion object {
        fun jclass(env: JniEnv) = env.findClass("java/lang/Thread")
    }
}
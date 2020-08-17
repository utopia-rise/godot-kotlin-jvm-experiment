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

// don't cache instances of this class
class Thread(handle: jobject) : JObject(handle) {
    private val classCache = mutableMapOf<String, JClass>()
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
        if (classCache.containsKey(className)) {
            return classCache[className]
        }
        // class loader uses `.` as separator instead of `/`
        return getContextClassLoader()?.loadClassOrNull(className.replace("/", "."))?.also {
            classCache[className] = it
        }
    }

    fun loadClass(className: String): JClass {
        return checkNotNull(loadClassOrNull(className)) { "Failed to find class $className in current thread!" }
    }

    companion object {
        fun jclass(env: JniEnv) = env.findClass("java/lang/Thread")
    }
}
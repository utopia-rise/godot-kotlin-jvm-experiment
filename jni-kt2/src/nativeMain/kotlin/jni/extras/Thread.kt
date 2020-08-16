package jni.extras

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

    companion object {
        fun jclass(env: JniEnv) = env.findClass("java/lang/Thread")
    }
}
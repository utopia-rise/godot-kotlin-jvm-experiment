package godot.loader.internal

import jni.JObject
import jni.JniEnv
import jni.extras.currentThread

class NativeKObject(_wrapped: JObject) {
    private val wrapped = _wrapped.newGlobalRef()

    fun _onInit(env: JniEnv) {
        val cls = jclass(env)
        val initMethod = cls.getMethodID("_onInit", "()V")
        wrapped.callVoidMethod(initMethod)
    }

    fun _onDestroy(env: JniEnv) {
        val cls = jclass(env)
        val destroyMethod = cls.getMethodID("_onDestroy", "()V")
        wrapped.callVoidMethod(destroyMethod)
        wrapped.deleteGlobalRef()
    }

    companion object {
        fun jclass(env: JniEnv) = env.currentThread().loadClass("godot.internal.KObject")
    }
}
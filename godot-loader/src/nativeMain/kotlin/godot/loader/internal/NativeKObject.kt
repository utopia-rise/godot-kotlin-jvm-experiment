package godot.loader.internal

import godot.loader.registry.NativeKFunc
import jni.JObject
import jni.JniEnv
import jni.extras.currentThread
import kotlinx.cinterop.*

class NativeKObject(_wrapped: JObject) {
    val wrapped = _wrapped.newGlobalRef()

    fun _onInit(env: JniEnv) {
        val cls = jclass(env)
        val initMethod = cls.getMethodID("_onInit", "()V")
        wrapped.callVoidMethod(initMethod)
    }

    fun _onDestroy(env: JniEnv) {
        val cls = jclass(env)
        val destroyMethod = cls.getMethodID("_onDestroy", "()V")
        wrapped.callVoidMethod(destroyMethod)
        dispose()
    }

    fun getRawPtr(env: JniEnv): COpaquePointer {
        val cls = NativeKFunc.jclass(env)
        val getClassNameMethod = cls.getMethodID("_getRawPtr", "()J")
        return wrapped.callLongMethod(getClassNameMethod).toCPointer()!!
    }

    fun dispose() {
        wrapped.deleteGlobalRef()

    }

    companion object {
        val SGN = "godot/internal/KObject"
        fun jclass(env: JniEnv) = env.currentThread().loadClass("godot.internal.KObject")
    }
}
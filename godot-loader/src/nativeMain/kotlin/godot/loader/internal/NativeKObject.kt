package godot.loader.internal

import jni.JObject
import jni.JniEnv
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.toCPointer

class NativeKObject(_wrapped: JObject) {
    val wrapped = _wrapped.newGlobalRef()

    fun _onInit(env: JniEnv) {
        val initMethod = getMethodId("_onInit", "()V")
        wrapped.callVoidMethod(initMethod)
    }

    fun _onDestroy(env: JniEnv) {
        val destroyMethod = getMethodId("_onDestroy", "()V")
        wrapped.callVoidMethod(destroyMethod)
        dispose()
    }

    fun getRawPtr(env: JniEnv): COpaquePointer {
        val getClassNameMethod = getMethodId("_getRawPtr", "()J")
        return wrapped.callLongMethod(getClassNameMethod).toCPointer()!!
    }

    fun dispose() {
        wrapped.deleteGlobalRef()
    }

    @ThreadLocal
    companion object : JObjectWrapper("godot.internal.KObject")
}
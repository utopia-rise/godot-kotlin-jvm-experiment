package godot.loader.registry

import jni.JObjectArray
import jni.JniEnv
import jni.NativeMethod
import jni.sys.JNIEnvVar
import jni.sys.jobject
import jni.sys.jobjectArray
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import kotlinx.cinterop.staticCFunction

@ThreadLocal
object NativeRegistry {
    lateinit var nativescriptHandle: COpaquePointer

    fun nativeMethods() = listOf(
        NativeMethod("registerAll", "([Lgodot/registry/ClassHandle;)V", staticCFunction(::_registerAll))
    )

    fun registerAll(env: JniEnv, handles: JObjectArray) {
        for (i in 0 until handles.length()) {
            registerClass(env, NativeClassHandle(handles[i]!!, false))
        }
    }

    private fun registerClass(env: JniEnv, handle: NativeClassHandle) {
        println("Registering class ${handle.getClassName(env)} with superClass ${handle.getSuperClass(env)}")
        handle.init(env, nativescriptHandle)
    }
}

fun _registerAll(env: CPointer<JNIEnvVar>, instance: jobject, handles: jobjectArray) {
    with(JniEnv(env.pointed)) {
        NativeRegistry.registerAll(this, JObjectArray(handles))
    }
}
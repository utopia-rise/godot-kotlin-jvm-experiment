package godot.loader.registry

import jni.JObjectArray
import jni.JniEnv
import kotlinx.cinterop.COpaquePointer

object NativeRegistry {
    fun registerAll(nativescriptHandle: COpaquePointer, env: JniEnv, handles: JObjectArray) {
        for (i in 0 until handles.length()) {
            registerClass(nativescriptHandle, env, NativeClassHandle(handles[i]!!, false))
        }
    }

    private fun registerClass(nativescriptHandle: COpaquePointer, env: JniEnv, handle: NativeClassHandle) {
        println("Registering class ${handle.getClassName(env)} with superClass ${handle.getSuperClass(env)}")
        handle.init(env, nativescriptHandle)
    }
}

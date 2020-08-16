package godot.loader.registry

import jni.JObject
import jni.JniEnv

class ClassHandle(val wrapped: JObject) {
    fun getClassName(env: JniEnv): String {
        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getClassName", "()Ljava/lang/String;")
        val className = wrapped.callObjectMethod(getClassNameMethod)?.toJString()?.toKString()
        checkNotNull(className) { "Failed to get className!" }
        return className
    }

    fun getSuperClass(env: JniEnv): String {
        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getSuperClass", "()Ljava/lang/String;")
        val className = wrapped.callObjectMethod(getClassNameMethod)?.toJString()?.toKString()
        checkNotNull(className) { "Failed to get superClass!" }
        return className
    }

    companion object {
        fun jclass(env: JniEnv) = env.findClass("godot/registry/ClassHandle")
    }
}
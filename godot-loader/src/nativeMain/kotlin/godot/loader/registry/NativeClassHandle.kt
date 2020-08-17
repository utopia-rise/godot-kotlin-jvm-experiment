package godot.loader.registry

import godot.gdnative.godot_instance_create_func
import godot.gdnative.godot_instance_destroy_func
import godot.loader.internal.Godot
import godot.loader.internal.NativeKObject
import godot.loader.internal.nullSafe
import jni.JObject
import jni.JString
import jni.JniEnv
import jni.extras.currentThread
import kotlinx.cinterop.*

class NativeClassHandle(_wrapped: JObject, private val isTool: Boolean) {
    private val wrapped = _wrapped.newGlobalRef()

    private val disposables = mutableListOf<COpaquePointer>()

    fun getClassName(env: JniEnv): String {
        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getClassName", "()Ljava/lang/String;")
        val className = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(className) { "Failed to get className!" }
        return className
    }

    fun getSuperClass(env: JniEnv): String {
        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getSuperClass", "()Ljava/lang/String;")
        val className = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(className) { "Failed to get superClass!" }
        return className
    }

    fun init(env: JniEnv, nativescriptHandle: COpaquePointer) {
        memScoped {
            val methodData = StableRef.create(this@NativeClassHandle).asCPointer()
            // register constructor and destructor
            val create = cValue<godot_instance_create_func> {
                create_func = staticCFunction(::createInstance)
                free_func = staticCFunction(::disposeClassHandle)
                method_data = methodData
            }
            val destroy = cValue<godot_instance_destroy_func> {
                destroy_func = staticCFunction(::destroyInstance)
                method_data = methodData
            }
            val registerMethod = if (isTool) {
                Godot.nativescript.godot_nativescript_register_tool_class
            } else {
                Godot.nativescript.godot_nativescript_register_class
            }
            nullSafe(registerMethod)(
                nativescriptHandle,
                getClassName(env).cstr.ptr,
                getSuperClass(env).cstr.ptr,
                create,
                destroy
            )
        }
    }

    fun wrap(env: JniEnv, ptr: COpaquePointer): NativeKObject {
        val cls = jclass(env)
        val wrapMethod = cls.getMethodID("wrap", "(J)Lgodot/internal/KObject;")
        val obj = wrapped.callObjectMethod(wrapMethod, ptr.rawValue.toLong())
        checkNotNull(obj) { "Failed to call ClassHandle.wrap(ptr)" }
        return NativeKObject(obj)
    }

    fun dispose() {
        wrapped.deleteGlobalRef()
        disposables.forEach { it.asStableRef<Any>().dispose() }
    }

    companion object {
        fun jclass(env: JniEnv) = env.currentThread().loadClass("godot.registry.ClassHandle")
    }
}
package godot.loader.registry

import godot.gdnative.godot_instance_create_func
import godot.gdnative.godot_instance_destroy_func
import godot.gdnative.godot_instance_method
import godot.gdnative.godot_method_attributes
import godot.loader.internal.*
import jni.JObject
import jni.JObjectArray
import jni.JString
import jni.JniEnv
import kotlinx.cinterop.*

class NativeClassHandle(_wrapped: JObject, private val isTool: Boolean) {
    private val wrapped = _wrapped.newGlobalRef()

    private val disposables = mutableListOf<COpaquePointer>()

    // local caches
    private var _className: String? = null
    private var _superClass: String? = null
    private var _functions: List<NativeKFunc>? = null

    fun getClassName(env: JniEnv): String {
        if (_className != null) {
            return _className!!
        }
        val getClassNameMethod = getMethodId("getClassName", "()Ljava/lang/String;")
        val className = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(className) { "Failed to get className!" }
        return className.also { _className = it }
    }

    fun getSuperClass(env: JniEnv): String {
        if (_superClass != null) {
            return _superClass!!
        }
        val getClassNameMethod = getMethodId("getSuperClass", "()Ljava/lang/String;")
        val superClass = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(superClass) { "Failed to get superClass!" }
        return superClass.also { _superClass = it }
    }

    fun getFunctions(env: JniEnv): List<NativeKFunc> {
        if (_functions != null) {
            return _functions!!
        }
        val getFunctionsMethod = getMethodId("getFunctions", "()[Lgodot/registry/KFunc;")
        val functions = wrapped.callObjectMethod(getFunctionsMethod)?.let { JObjectArray.unsafeCast(it) }
        checkNotNull(functions) { "Failed to retrieve functions!" }

        val tmp = mutableListOf<NativeKFunc>()
        for (i in 0 until functions.length()) {
            tmp.add(NativeKFunc(functions[i]!!))
        }
        return tmp.also { _functions = it }
    }

    fun init(env: JniEnv, nativescriptHandle: COpaquePointer) {
        val className = getClassName(env)
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
                className.cstr.ptr,
                getSuperClass(env).cstr.ptr,
                create,
                destroy
            )
        }

        for (func in getFunctions(env)) {
            registerFunction(nativescriptHandle, className, func.getRegistrationName(env), StableRef.create(func).asCPointer())
        }
    }

    private fun registerFunction(nativescriptHandle: COpaquePointer, className: String, funcName: String, funcRef: COpaquePointer) {
        println("Registering function: $funcName")
        disposables.add(funcRef)
        memScoped {
            val attribs = cValue<godot_method_attributes> {
                // rpc_type = toGodotRpcMode(rpcMode)
            }

            val instanceMethod = cValue<godot_instance_method> {
                method_data = funcRef
                this.method = staticCFunction(::invokeMethod)
            }

            nullSafe(Godot.nativescript.godot_nativescript_register_method)(
                nativescriptHandle,
                className.cstr.ptr,
                funcName.cstr.ptr, //not using `camelcaseToUnderscore` to prevent a call to godot for each function
                attribs,
                instanceMethod
            )
        }
    }


    fun wrap(env: JniEnv, ptr: COpaquePointer): NativeKObject {
        val wrapMethod = getMethodId("wrap", "(J)Lgodot/internal/KObject;")
        val obj = wrapped.callObjectMethod(wrapMethod, ptr.rawValue.toLong())
        checkNotNull(obj) { "Failed to call ClassHandle.wrap(ptr)" }
        return NativeKObject(obj)
    }

    fun dispose() {
        wrapped.deleteGlobalRef()
        disposables.forEach {
            val ref = it.asStableRef<Disposable>()
            val disposable = ref.get()
            // clean up disposable
            disposable.dispose()
            // finally clean up the ref itself
            ref.dispose()
        }
    }

    @ThreadLocal
    companion object : JObjectWrapper("godot.registry.ClassHandle")
}
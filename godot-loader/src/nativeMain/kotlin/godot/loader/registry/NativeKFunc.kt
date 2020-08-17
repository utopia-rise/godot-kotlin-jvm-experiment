package godot.loader.registry

import godot.gdnative.godot_variant
import godot.loader.internal.*
import jni.JObject
import jni.JString
import jni.JniEnv
import jni.extras.currentThread
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import kotlinx.cinterop.invoke
import kotlinx.cinterop.ptr

class NativeKFunc(_wrapped: JObject) : Disposable {
    val wrapped = _wrapped.newGlobalRef()

    private var _name: String? = null
    private var _registrationName: String? = null
    private var _parameterCount: Int? = null

    fun getName(env: JniEnv): String {
        if (_name != null) {
            return _name!!
        }

        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getName", "()Ljava/lang/String;")
        val name = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(name) { "Failed to get name!" }
        return name.also { _name = it }
    }

    fun getRegistrationName(env: JniEnv): String {
        if (_registrationName != null) {
            return _registrationName!!
        }

        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getRegistrationName", "()Ljava/lang/String;")
        val registrationName = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(registrationName) { "Failed to get registration name!" }
        return registrationName.also { _registrationName = it }
    }


    fun getParameterCount(env: JniEnv): Int {
        if (_parameterCount != null) {
            return _parameterCount!!
        }

        val cls = jclass(env)
        val getClassNameMethod = cls.getMethodID("getParameterCount", "()I")
        val parameterCount = wrapped.callIntMethod(getClassNameMethod)
        checkNotNull(parameterCount) { "Failed to get parameter count!" }
        return parameterCount.also { _parameterCount = it }
    }

    operator fun invoke(env: JniEnv, instance: NativeKObject, args: Array<CValue<godot_variant>>): CValue<godot_variant> {
        val cls = jclass(env)
        val kvariantCls = env.currentThread().loadClass("godot.internal.KVariant")
        val jvmArgs = kvariantCls.newObjectArray(0)
        val invokeMethod = cls.getMethodID(
            "invoke",
            "(L${NativeKObject.BINARY_NAME};[L${NativeKVariant.BINARY_NAME};)L${NativeKVariant.BINARY_NAME};"
        )
        println("calling method!")
        wrapped.callObjectMethod(invokeMethod, instance.wrapped, jvmArgs)
        println("done!")
        return cValue {
            nullSafe(Godot.gdnative.godot_variant_new_nil)(ptr)
        }
    }

    override fun dispose() {
        wrapped.deleteGlobalRef()
    }

    companion object {
        const val BINARY_NAME = "godot/registry/KFunc"
        fun jclass(env: JniEnv) = env.currentThread().loadClass("godot.registry.KFunc")
    }
}
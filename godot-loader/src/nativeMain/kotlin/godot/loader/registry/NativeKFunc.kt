package godot.loader.registry

import godot.loader.internal.Disposable
import godot.loader.internal.NativeKObject
import godot.loader.internal.NativeKVariant
import jni.JObject
import jni.JString
import jni.JniEnv
import jni.extras.currentThread

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
        return parameterCount.also { _parameterCount = it }
    }

    operator fun invoke(env: JniEnv, instance: NativeKObject, args: Array<NativeKVariant>): NativeKVariant {
        val cls = jclass(env)
        val kvariantCls = env.currentThread().loadClass("godot.internal.KVariant")
        val jvmArgs = kvariantCls.newObjectArray(args.size)
        args.forEachIndexed { i, v -> jvmArgs[i] = v.toJava() }
        val invokeMethod = cls.getMethodID(
            "invoke",
            "(L${NativeKObject.SGN};[L${NativeKVariant.SGN};)L${NativeKVariant.SGN};"
        )
        val result = wrapped.callObjectMethod(invokeMethod, instance.wrapped, jvmArgs)
        checkNotNull(result) { "Expecting a non-null KVariant!" }
        return NativeKVariant.fromJava(result)
    }

    override fun dispose() {
        wrapped.deleteGlobalRef()
    }

    companion object {
        const val SGN = "godot/registry/KFunc"
        fun jclass(env: JniEnv) = env.currentThread().loadClass("godot.registry.KFunc")
    }
}
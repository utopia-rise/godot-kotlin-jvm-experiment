package godot.loader.registry

import godot.loader.internal.Disposable
import godot.loader.internal.JObjectWrapper
import godot.loader.internal.NativeKObject
import godot.loader.internal.NativeKVariant
import jni.JObject
import jni.JString
import jni.JniEnv

class NativeKFunc(_wrapped: JObject) : Disposable {
    val wrapped = _wrapped.newGlobalRef()

    private var _name: String? = null
    private var _registrationName: String? = null
    private var _parameterCount: Int? = null

    fun getName(env: JniEnv): String {
        if (_name != null) {
            return _name!!
        }

        val getClassNameMethod = getMethodId("getName", "()Ljava/lang/String;")
        val name = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(name) { "Failed to get name!" }
        return name.also { _name = it }
    }

    fun getRegistrationName(env: JniEnv): String {
        if (_registrationName != null) {
            return _registrationName!!
        }

        val getClassNameMethod = getMethodId("getRegistrationName", "()Ljava/lang/String;")
        val registrationName = wrapped.callObjectMethod(getClassNameMethod)?.let(JString.Companion::unsafeCast)?.toKString()
        checkNotNull(registrationName) { "Failed to get registration name!" }
        return registrationName.also { _registrationName = it }
    }


    fun getParameterCount(env: JniEnv): Int {
        if (_parameterCount != null) {
            return _parameterCount!!
        }

        val getClassNameMethod = getMethodId("getParameterCount", "()I")
        val parameterCount = wrapped.callIntMethod(getClassNameMethod)
        return parameterCount.also { _parameterCount = it }
    }

    operator fun invoke(env: JniEnv, instance: NativeKObject, args: Array<NativeKVariant>): NativeKVariant {
        val kvariantCls = NativeKVariant.jclass
        val jvmArgs = kvariantCls.newObjectArray(args.size)

        args.forEachIndexed { i, v -> jvmArgs[i] = v.toJava() }
        val invokeMethod = jclass.getMethodId(
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

    @ThreadLocal
    companion object : JObjectWrapper("godot.registry.KFunc")
}
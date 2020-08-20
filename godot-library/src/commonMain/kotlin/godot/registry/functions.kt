package godot.registry

import godot.internal.BindingContext
import godot.internal.KObject
import godot.internal.camelToSnakeCase
import godot.internal.meta.JniExposed
import godot.wire.TValue

abstract class KFunc<T : KObject, R>(
    @JniExposed val name: String,
    @JniExposed val parameterCount: Int
) {
    @JniExposed
    val registrationName = name.camelToSnakeCase()

    @JniExposed
    abstract fun invoke(instance: T): Boolean
}

class KFunc0<T: KObject, R>(
    name: String,
    private val func: T.() -> R,
    private val returnValueConverter: (R) -> TValue
) : KFunc<T, R>(name, 0) {
    override fun invoke(instance: T): Boolean {
        val returnValue =  returnValueConverter(
            func(instance)
        )
        return BindingContext.transferContext.writeReturnValue(returnValue)
    }
}

class KFunc1<T: KObject, P0, R>(
    name: String,
    private val func: T.(P0) -> R,
    private val returnValueConverter: (R) -> TValue,
    private val argConverters: List<(TValue) -> Any>
) : KFunc<T, R>(name, 1) {
    override fun invoke(instance: T): Boolean {
        val args = BindingContext.transferContext.readArguments()
        val returnValue = returnValueConverter(
            func(
                instance,
                argConverters[0](args[0]) as P0
            )
        )
        return BindingContext.transferContext.writeReturnValue(returnValue)
    }
}
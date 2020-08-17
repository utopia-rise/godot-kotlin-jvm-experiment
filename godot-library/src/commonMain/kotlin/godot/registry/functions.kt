package godot.registry

import godot.internal.JniExposed
import godot.internal.KObject
import godot.internal.KVariant
import godot.internal.camelToSnakeCase

abstract class KFunc<T : KObject, R>(
    @JniExposed val name: String,
    @JniExposed val parameterCount: Int
) {
    @JniExposed
    val registrationName = name.camelToSnakeCase()

    @JniExposed
    abstract fun invoke(instance: T, args: Array<KVariant>): KVariant
}

class KFunc0<T: KObject, R>(
    name: String,
    private val func: T.() -> R,
    private val returnValueConverter: (R) -> KVariant
) : KFunc<T, R>(name, 0) {
    override fun invoke(instance: T, args: Array<KVariant>): KVariant {
        return returnValueConverter(
            func(instance)
        )
    }
}

class KFunc1<T: KObject, P0, R>(
    name: String,
    private val func: T.(P0) -> R,
    private val resultConverter: (R) -> KVariant,
    private val argConverters: List<(KVariant) -> P0>
) : KFunc<T, R>(name, 1) {
    override fun invoke(instance: T, args: Array<KVariant>): KVariant {
        return resultConverter(
            func(
                instance,
                argConverters[0](args[0])
            )
        )
    }
}
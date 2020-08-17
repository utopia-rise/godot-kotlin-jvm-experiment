package godot.registry

import godot.internal.KObject

class ClassBuilderDsl<T: KObject>(private val handle: ClassHandle<T>) {
    fun <K: KFunc<T, *>> function(func: K) {
        handle.registerFunction(func)
    }
}
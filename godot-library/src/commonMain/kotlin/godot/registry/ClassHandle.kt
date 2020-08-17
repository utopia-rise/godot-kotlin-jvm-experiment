package godot.registry

import godot.internal.BindingContext
import godot.internal.JniExposed
import godot.internal.KObject
import godot.internal.VoidPtr

class ClassHandle<T: KObject>(@JniExposed val className: String,
                              @JniExposed val superClass: String,
                              private val constructor: () -> T) {

    private val _functions = mutableListOf<KFunc<T, *>>()

    // for easy access in JNI
    @JniExposed
    val functions: Array<KFunc<T, *>>
        get() = _functions.toTypedArray()

    fun <K: KFunc<T, *>> registerFunction(func: K) {
        _functions.add(func)
    }

    fun wrap(ptr: VoidPtr): T {
        return BindingContext.instantiateWith(ptr, constructor)
    }
}
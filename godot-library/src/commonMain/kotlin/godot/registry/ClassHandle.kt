package godot.registry

import godot.internal.BindingContext
import godot.internal.KObject
import godot.internal.VoidPtr

class ClassHandle<T: KObject>(val className: String,
                              val superClass: String,
                              val constructor: () -> T) {

    fun wrap(ptr: VoidPtr): T {
        return BindingContext.instantiateWith(ptr, constructor)
    }
}
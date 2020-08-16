package godot.registry

import godot.internal.KObject

class Constructor<T: KObject>(val factory: () -> T) {
    fun invoke() = factory()
}
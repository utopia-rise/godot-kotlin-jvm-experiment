package godot

import godot.registry.Registry

abstract class GodotEntry {
    protected abstract fun Registry.registerClasses()

    fun init() {
        val registry = Registry()
        registry.registerClasses()
        registry.commit()
    }
}
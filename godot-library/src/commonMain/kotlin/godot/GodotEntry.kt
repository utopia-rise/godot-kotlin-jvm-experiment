package godot

import godot.registry.ClassHandle
import godot.registry.Registry

abstract class GodotEntry {
    protected abstract fun Registry.registerClasses()

    fun init(): Array<ClassHandle<*>> {
        val registry = Registry()
        registry.registerClasses()
        return registry.getAllClasses().toTypedArray()
    }
}
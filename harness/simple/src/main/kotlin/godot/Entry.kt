package godot

import godot.registry.Registry
import simple.Simple

class Entry: GodotEntry() {
    override fun Registry.registerClasses() {
        println("Registering classes!")
        registerClass("Simple", "Object", ::Simple)
    }
}
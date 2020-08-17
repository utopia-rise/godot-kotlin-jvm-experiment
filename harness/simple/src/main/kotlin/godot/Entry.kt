package godot

import godot.internal.KVariant
import godot.registry.KFunc0
import godot.registry.Registry
import simple.Simple

class Entry: GodotEntry() {
    override fun Registry.registerClasses() {
        println("Registering classes!")
        registerClass("Simple", "Spatial", ::Simple) {
            function(KFunc0("_ready", Simple::_ready) { KVariant.nil() })
        }
    }
}
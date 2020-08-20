package godot

import godot.registry.KFunc0
import godot.registry.KFunc1
import godot.registry.Registry
import godot.wire.TValue
import simple.Simple

class Entry: GodotEntry() {
    override fun Registry.registerClasses() {
        println("Registering classes!")
        registerClass("Simple", "Spatial", ::Simple) {
            function(KFunc0("_ready", Simple::_ready, ::TValue))
            function(KFunc1("_process", Simple::_process, ::TValue, listOf(TValue::asDouble)))
            function(KFunc1("longMethod", Simple::longMethod, ::TValue, listOf(TValue::asLong)))
            function(KFunc1("boolMethod", Simple::boolMethod, ::TValue, listOf(TValue::asBool)))
            function(KFunc1("stringMethod", Simple::stringMethod, ::TValue, listOf(TValue::asString)))
        }
    }
}
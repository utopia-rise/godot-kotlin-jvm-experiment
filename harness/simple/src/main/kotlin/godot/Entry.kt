package godot

import godot.internal.KVariant
import godot.registry.KFunc0
import godot.registry.KFunc1
import godot.registry.Registry
import simple.Simple

class Entry: GodotEntry() {
    override fun Registry.registerClasses() {
        println("Registering classes!")
        registerClass("Simple", "Spatial", ::Simple) {
            function(KFunc0("_ready", Simple::_ready, { KVariant.nil() }))
//            function(KFunc1("_process", Simple::_process, { KVariant.nil() }, listOf(KVariant::asDouble)))
//            function(KFunc1("longMethod", Simple::longMethod, { KVariant.nil() }, listOf(KVariant::asLong)))
//            function(KFunc1("boolMethod", Simple::boolMethod, { KVariant.nil() }, listOf(KVariant::asBool)))
//            function(KFunc1("stringMethod", Simple::stringMethod, { KVariant.nil() }, listOf(KVariant::asString)))
        }
    }
}
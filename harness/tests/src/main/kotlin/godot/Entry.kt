package godot

import godot.tests.Invocation
import godot.registry.KFunc1
import godot.registry.Registry
import godot.wire.TValue

class Entry : GodotEntry() {
    override fun Registry.registerClasses() {
        registerClass("Invocation", "Spatial", ::Invocation) {
            function(KFunc1("longValue", Invocation::longValue, ::TValue, TValue::asLong))
            function(KFunc1("intValue", Invocation::intValue, ::TValue, TValue::asInt))
            function(KFunc1("floatValue", Invocation::floatValue, ::TValue, TValue::asFloat))
            function(KFunc1("doubleValue", Invocation::doubleValue, ::TValue, TValue::asDouble))
            function(KFunc1("boolValue", Invocation::boolValue, ::TValue, TValue::asBool))
            function(KFunc1("stringValue", Invocation::stringValue, ::TValue, TValue::asString))
            function(KFunc1("vector2Value", Invocation::vector2Value, ::TValue, TValue::asVector2))
        }
    }
}
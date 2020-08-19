package godot

import godot.benchmark.Simple
import godot.registry.KFunc0
import godot.registry.Registry
import godot.wire.TValue

class Entry : GodotEntry() {
    override fun Registry.registerClasses() {
        registerClass("Simple", "Object", ::Simple) {
            function(KFunc0("benchmarkSimpleAdd", Simple::benchmarkSimpleAdd) { TValue(it) })
            function(KFunc0("benchmarkAvg", Simple::benchmarkAvg) { TValue(it) })
        }
    }
}
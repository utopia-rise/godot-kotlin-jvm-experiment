package godot

import godot.benchmark.Simple
import godot.internal.KVariant
import godot.registry.KFunc0
import godot.registry.Registry

class Entry : GodotEntry() {
    override fun Registry.registerClasses() {
        registerClass("Simple", "Object", ::Simple) {
            function(KFunc0("benchmarkSimpleAdd", Simple::benchmarkSimpleAdd) { KVariant.from(it) })
        }
    }
}
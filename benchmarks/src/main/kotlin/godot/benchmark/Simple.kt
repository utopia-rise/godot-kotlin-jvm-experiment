package godot.benchmark

import godot.internal.KObject

class Simple : KObject() {
    fun benchmarkSimpleAdd(): Int {
        val a = 1
        val b = 2
        return a + b
    }
}
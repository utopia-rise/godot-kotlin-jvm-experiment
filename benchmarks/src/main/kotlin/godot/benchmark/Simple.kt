package godot.benchmark

import godot.core.Vector2
import godot.internal.KObject

class Simple : KObject() {
    fun benchmarkSimpleAdd(): Int {
        val a = 1
        val b = 2
        return a + b
    }

    fun benchmarkAvg(): Int {
        val size = 10000
        var total = 0
        for (i in 0 until size) {
            total += i
        }
        return total / size
    }

    fun benchmarkVectors2Only(): Vector2 {
        var s = Vector2()
        for(i in 0 until 1000) {
            val v = Vector2(i, i)
            s += v
        }

        return s
    }

}
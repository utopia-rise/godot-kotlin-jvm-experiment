package godot.tests

import godot.core.Vector2
import godot.internal.KObject

class Invocation : KObject() {
    fun longValue(value: Long) = value
    fun intValue(value: Int) = value
    fun floatValue(value: Float) = value
    fun doubleValue(value: Double) = value
    fun stringValue(value: String) = value
    fun boolValue(value: Boolean) = value
    fun vector2Value(value: Vector2) = value
}
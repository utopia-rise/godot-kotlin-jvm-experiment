package godot.tests

import godot.core.Rect2
import godot.core.Vector2
import godot.core.Vector3
import godot.internal.KObject

class Invocation : KObject() {
    fun longValue(value: Long) = value
    fun intValue(value: Int) = value
    fun floatValue(value: Float) = value
    fun doubleValue(value: Double) = value
    fun stringValue(value: String) = value
    fun boolValue(value: Boolean) = value
    fun vector2Value(value: Vector2) = value
    fun rect2Value(value: Rect2) = value
    fun vector3Value(value: Vector3) = value
}
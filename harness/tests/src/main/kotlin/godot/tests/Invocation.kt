package godot.tests

import godot.core.*
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
    fun transform2DValue(value: Transform2D) = value
    fun planeValue(value: Plane) = value
    fun quatValue(value: Quat) = value
    fun aabbValue(value: AABB) = value
    fun basisValue(value: Basis) = value
    fun transformValue(value: Transform) = value
}
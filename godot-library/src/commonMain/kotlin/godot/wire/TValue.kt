package godot.wire

import godot.core.*

expect class TValue {
    constructor(value: Unit)
    constructor(value: Int)
    constructor(value: Float)
    constructor(value: Long)
    constructor(value: Double)
    constructor(value: String)
    constructor(value: Boolean)
    constructor(value: Vector2)
    constructor(value: Rect2)
    constructor(value: Vector3)
    constructor(value: Transform2D)
    constructor(value: Plane)
    constructor(value: Quat)
    constructor(value: AABB)
    constructor(value: Basis)
    constructor(value: Transform)

    fun asNil(): Unit
    fun asInt(): Int
    fun asLong(): Long
    fun asFloat(): Float
    fun asDouble(): Double
    fun asString(): String
    fun asBool(): Boolean
    fun asVector2(): Vector2
    fun asRect2(): Rect2
    fun asVector3(): Vector3
    fun asTransform2D(): Transform2D
    fun asPlane(): Plane
    fun asQuat(): Quat
    fun asAABB(): AABB
    fun asBasis(): Basis
    fun asTransform(): Transform

    enum class Type {
        NIL,
        LONG,
        DOUBLE,
        STRING,
        BOOL,
        VECTOR2,
        RECT2,
        VECTOR3,
        TRANSFORM2D,
        PLANE,
        QUAT,
        AABB,
        BASIS,
        TRANSFORM
    }
}
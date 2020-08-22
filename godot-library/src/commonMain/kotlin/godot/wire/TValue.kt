package godot.wire

import godot.core.Rect2
import godot.core.Vector2
import godot.core.Vector3

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

    enum class Type {
        NIL,
        LONG,
        DOUBLE,
        STRING,
        BOOL,
        VECTOR2,
        RECT2,
        VECTOR3
    }
}
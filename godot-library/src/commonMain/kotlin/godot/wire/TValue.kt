package godot.wire

import godot.core.Vector2

expect class TValue {
    constructor(value: Unit)
    constructor(value: Int)
    constructor(value: Float)
    constructor(value: Long)
    constructor(value: Double)
    constructor(value: String)
    constructor(value: Boolean)
    constructor(value: Vector2)

    fun asNil(): Unit
    fun asInt(): Int
    fun asLong(): Long
    fun asFloat(): Float
    fun asDouble(): Double
    fun asString(): String
    fun asBool(): Boolean
    fun asVector2(): Vector2

    enum class Type {
        NIL,
        LONG,
        DOUBLE,
        STRING,
        BOOL,
        VECTOR2
    }
}
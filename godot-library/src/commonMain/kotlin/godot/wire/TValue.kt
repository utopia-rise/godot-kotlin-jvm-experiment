package godot.wire

expect class TValue {
    constructor(value: Int)
    constructor(value: Float)
    constructor(value: Long)
    constructor(value: Double)
    constructor(value: String)
    constructor(value: Boolean)
    constructor(value: Unit)

    fun asInt(): Int
    fun asLong(): Long
    fun asFloat(): Float
    fun asDouble(): Double
    fun asString(): String
    fun asBool(): Boolean
    fun asUnit(): Unit
}
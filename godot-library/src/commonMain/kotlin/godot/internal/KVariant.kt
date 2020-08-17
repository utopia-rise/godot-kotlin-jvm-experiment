package godot.internal

class KVariant (val type: Type, private val value: Any?) {
    enum class Type {
        NIL,
        BOOL,
        OBJECT,
        DOUBLE,
        LONG,
        STRING
    }

    fun asBool() = value as Boolean

    fun asLong() = value as Long
    fun asInt() = asLong().toInt()

    fun asDouble() = value as Double
    fun asFloat() = asDouble().toFloat()

    fun asString() = value as String

    fun <T: KObject> asObject(): T {
        TODO()
    }

    companion object {
        fun nil() = KVariant(Type.NIL, null)

        fun from(value: Boolean) = KVariant(Type.BOOL, value)

        fun from(value: Int) = KVariant(Type.LONG, value.toLong())
        fun from(value: Long) =  KVariant(Type.LONG, value)

        fun from(value: Float) = KVariant(Type.DOUBLE, value.toDouble())
        fun from(value: Double) = KVariant(Type.DOUBLE, value)

        fun from(value: String) = KVariant(Type.STRING, value)

        fun from(value: KObject) = KVariant(Type.OBJECT, value)
    }
}
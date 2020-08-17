package godot.internal

import kotlin.jvm.JvmStatic

class KVariant (val type: Type, private val value: Any?) {
    enum class Type {
        NIL,
        LONG,
        DOUBLE,
        BOOL,
        OBJECT,
        STRING
    }

    @JniExposed
    val typeOrdinal = type.ordinal

    @JniExposed
    fun asBool() = value as Boolean

    @JniExposed
    fun asLong() = value as Long
    fun asInt() = asLong().toInt()

    @JniExposed
    fun asDouble() = value as Double
    fun asFloat() = asDouble().toFloat()

    @JniExposed
    fun asString() = value as String

    @JniExposed
    fun <T: KObject> asObject(): T? {
        TODO()
    }

    companion object {
        @JvmStatic
        @JniExposed
        fun nil() = KVariant(Type.NIL, null)

        @JvmStatic
        @JniExposed
        fun from(value: Boolean) = KVariant(Type.BOOL, value)

        @JvmStatic
        @JniExposed
        fun from(value: Int) = KVariant(Type.LONG, value.toLong())
        @JvmStatic
        @JniExposed
        fun from(value: Long) =  KVariant(Type.LONG, value)

        @JvmStatic
        @JniExposed
        fun from(value: Float) = KVariant(Type.DOUBLE, value.toDouble())
        @JvmStatic
        @JniExposed
        fun from(value: Double) = KVariant(Type.DOUBLE, value)

        @JvmStatic
        @JniExposed
        fun from(value: String) = KVariant(Type.STRING, value)

        @JvmStatic
        @JniExposed
        fun from(value: KObject?): KVariant {
            return if (value == null) {
                nil()
            } else {
                KVariant(Type.OBJECT, value)
            }
        }

        @JvmStatic
        fun fromRawPtr(ptr: VoidPtr): KVariant {
            require(ptr != nullptr) { "Can't create a variant from a null ptr!" }
            return KVariant(Type.OBJECT, TypeManager.wrap(ptr))
        }
    }
}
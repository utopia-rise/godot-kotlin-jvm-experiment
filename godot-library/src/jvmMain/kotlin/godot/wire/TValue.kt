package godot.wire

actual class TValue {
    var data: Wire.KVariant

    constructor(data: Wire.KVariant) {
        this.data = data
    }

    actual constructor(value: Int) : this(value.toLong())

    actual constructor(value: Long) {
        data = build { setLongValue(value) }
    }

    actual constructor(value: Float) : this(value.toDouble())

    actual constructor(value: Double) {
        data = build { setRealValue(value) }
    }

    actual constructor(value: String) {
        data = build { setStringValue(value) }
    }

    actual constructor(value: Boolean) {
        data = build { setBoolValue(value) }
    }

    actual constructor(value: Unit) {
        data = build { setUnitValue(0) }
    }

    actual fun asInt() = asLong().toInt()

    actual fun asLong(): Long {
        return data.longValue
    }

    actual fun asFloat() = asDouble().toFloat()

    actual fun asDouble(): Double {
        return data.realValue
    }

    actual fun asString(): String {
        return data.stringValue
    }

    actual fun asBool(): Boolean {
        return data.boolValue
    }

    actual fun asUnit(): Unit {
        check(data.typeCase == Wire.KVariant.TypeCase.UNIT_VALUE) {
            "Expecting a UNIT but got ${data.typeCase}"
        }
        return Unit
    }

    private inline fun build(cb: Wire.KVariant.Builder.() -> Unit): Wire.KVariant {
        val builder = Wire.KVariant.newBuilder()
        builder.cb()
        return builder.build()
    }

}
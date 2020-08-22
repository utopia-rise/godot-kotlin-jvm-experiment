package godot.wire

import godot.core.Rect2
import godot.core.Vector2
import godot.core.Vector3
import godot.internal.toGodotReal
import godot.internal.toRealT

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
        data = build { setNilValue(0) }
    }

    actual constructor(value: Vector2) {
        data = build {
            val vec2 = Wire.Vector2.newBuilder()
                .setX(value.x.toGodotReal())
                .setY(value.y.toGodotReal())
                .build()

            setVector2Value(vec2)
        }
    }

    actual constructor(value: Rect2) {
        data = build {
            val position = Wire.Vector2.newBuilder()
                .setX(value.position.x.toGodotReal())
                .setY(value.position.y.toGodotReal())
                .build()
            val size = Wire.Vector2.newBuilder()
                .setX(value.size.x.toGodotReal())
                .setY(value.size.y.toGodotReal())
                .build()
            val rect2 = Wire.Rect2.newBuilder()
                .setPosition(position)
                .setSize(size)
                .build()

            setRect2Value(rect2)
        }
    }

    actual constructor(value: Vector3) {
        data = build {
            val vec3 = Wire.Vector3.newBuilder()
                .setX(value.x.toGodotReal())
                .setY(value.y.toGodotReal())
                .setZ(value.z.toGodotReal())
                .build()

            setVector3Value(vec3)
        }
    }

    actual fun asNil(): Unit {
        check(data.typeCase == Wire.KVariant.TypeCase.NIL_VALUE) {
            "Expecting a NIL but got ${data.typeCase}"
        }
        return Unit
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

    actual fun asVector2(): Vector2 {
        val vec2 = data.vector2Value
        return Vector2(vec2.x, vec2.y)
    }

    actual fun asRect2(): Rect2 {
        val rect2 = data.rect2Value
        return Rect2(
            rect2.position.x.toRealT(),
            rect2.position.y.toRealT(),
            rect2.size.x.toRealT(),
            rect2.size.y.toRealT()
        )
    }

    actual fun asVector3(): Vector3 {
        val vec3 = data.vector3Value
        return Vector3(vec3.x, vec3.y, vec3.z)
    }

    private inline fun build(cb: Wire.KVariant.Builder.() -> Unit): Wire.KVariant {
        val builder = Wire.KVariant.newBuilder()
        builder.cb()
        return builder.build()
    }

    actual enum class Type(val typeCase: Wire.KVariant.TypeCase) {
        NIL(Wire.KVariant.TypeCase.NIL_VALUE),
        LONG(Wire.KVariant.TypeCase.LONG_VALUE),
        DOUBLE(Wire.KVariant.TypeCase.LONG_VALUE),
        STRING(Wire.KVariant.TypeCase.LONG_VALUE),
        BOOL(Wire.KVariant.TypeCase.LONG_VALUE),
        VECTOR2(Wire.KVariant.TypeCase.VECTOR2_VALUE),
        RECT2(Wire.KVariant.TypeCase.RECT2_VALUE),
        VECTOR3(Wire.KVariant.TypeCase.VECTOR3_VALUE),
    }

}
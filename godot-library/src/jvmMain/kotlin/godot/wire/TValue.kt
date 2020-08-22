package godot.wire

import godot.core.*
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
            setVector2Value(value.toWireVector2())
        }
    }

    actual constructor(value: Rect2) {
        data = build {
            val rect2 = Wire.Rect2.newBuilder()
                .setPosition(value.position.toWireVector2())
                .setSize(value.size.toWireVector2())
                .build()

            setRect2Value(rect2)
        }
    }

    actual constructor(value: Vector3) {
        data = build {
            setVector3Value(value.toWireVector3())
        }
    }

    actual constructor(value: Transform2D) {
        data = build {
            val transform2D = Wire.Transform2D.newBuilder()
                .setX(value.x.toWireVector2())
                .setY(value.y.toWireVector2())
                .setOrigin(value.origin.toWireVector2())
                .build()
            setTransform2DValue(transform2D)
        }
    }

    actual constructor(value: Plane) {
        data = build {
            val plane = Wire.Plane.newBuilder()
                .setNormal(value.normal.toWireVector3())
                .setD(value.d.toGodotReal())
                .build()

            setPlaneValue(plane)
        }
    }

    actual constructor(value: Quat) {
        data = build {
            val quat = Wire.Quat.newBuilder()
                .setX(value.x.toGodotReal())
                .setY(value.y.toGodotReal())
                .setZ(value.z.toGodotReal())
                .setW(value.w.toGodotReal())
                .build()

            setQuatValue(quat)
        }
    }

    actual constructor(value: AABB) {
        data = build {
            val aabb = Wire.AABB.newBuilder()
                .setPosition(value.position.toWireVector3())
                .setSize(value.size.toWireVector3())
                .build()

            setAabbValue(aabb)
        }
    }

    actual constructor(value: Basis) {
        data = build {
            setBasisValue(value.toWireBasis())
        }
    }

    actual constructor(value: Transform) {
        data = build {
            val transform = Wire.Transform.newBuilder()
                .setBasis(value.basis.toWireBasis())
                .setOrigin(value.origin.toWireVector3())
                .build()

            setTransformValue(transform)
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

    actual fun asTransform2D(): Transform2D {
        val transform2D = data.transform2DValue
        val x = transform2D.x.toKVector2()
        val y = transform2D.y.toKVector2()
        val origin = transform2D.origin.toKVector2()
        return Transform2D(x, y, origin)
    }

    actual fun asPlane(): Plane {
        val plane = data.planeValue
        val normal = plane.normal.toKVector3()
        val d = plane.d.toRealT()
        return Plane(normal, d)
    }

    actual fun asQuat(): Quat {
        val quat = data.quatValue
        val x = quat.x.toRealT()
        val y = quat.y.toRealT()
        val z = quat.z.toRealT()
        val w = quat.w.toRealT()

        return Quat(x, y, z, w)
    }

    actual fun asAABB(): AABB {
        val aabb = data.aabbValue
        val position = aabb.position.toKVector3()
        val size = aabb.size.toKVector3()
        return AABB(position, size)
    }

    actual fun asBasis(): Basis {
        return data.basisValue.toKBasis()
    }

    actual fun asTransform(): Transform {
        val transform = data.transformValue
        val basis = transform.basis.toKBasis()
        val origin = transform.origin.toKVector3()
        return Transform(basis, origin)
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
        TRANSFORM2D(Wire.KVariant.TypeCase.TRANSFORM2D_VALUE),
        PLANE(Wire.KVariant.TypeCase.PLANE_VALUE),
        QUAT(Wire.KVariant.TypeCase.QUAT_VALUE),
        AABB(Wire.KVariant.TypeCase.AABB_VALUE),
        BASIS(Wire.KVariant.TypeCase.BASIS_VALUE),
        TRANSFORM(Wire.KVariant.TypeCase.TRANSFORM_VALUE),
    }

    companion object {
        fun Vector2.toWireVector2(): Wire.Vector2 {
            return Wire.Vector2.newBuilder()
                .setX(x.toGodotReal())
                .setY(y.toGodotReal())
                .build()
        }

        fun Vector3.toWireVector3(): Wire.Vector3 {
            return Wire.Vector3.newBuilder()
                .setX(x.toGodotReal())
                .setY(y.toGodotReal())
                .setZ(z.toGodotReal())
                .build()
        }

        fun Basis.toWireBasis(): Wire.Basis {
            // read the internal values directly
            return Wire.Basis.newBuilder()
                .setX(_x.toWireVector3())
                .setY(_y.toWireVector3())
                .setZ(_z.toWireVector3())
                .build()
        }

        fun Wire.Vector2.toKVector2(): Vector2 {
            return Vector2(x, y)
        }

        fun Wire.Vector3.toKVector3(): Vector3 {
            return Vector3(x, y, z)
        }

        fun Wire.Basis.toKBasis(): Basis {
            // write to the internal values directly
            return Basis().also {
                it._x = Vector3(this.x.x, this.x.y, this.x.z)
                it._y = Vector3(this.y.x, this.y.y, this.y.z)
                it._z = Vector3(this.z.x, this.z.y, this.z.z)
            }
        }
    }
}
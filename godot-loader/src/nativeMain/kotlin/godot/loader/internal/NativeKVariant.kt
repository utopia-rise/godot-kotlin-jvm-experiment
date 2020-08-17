package godot.loader.internal

import godot.gdnative.godot_variant
import godot.gdnative.godot_variant_type.*
import godot.loader.registry.NativeKFunc
import jni.JObject
import jni.JniEnv
import jni.extras.currentThread
import kotlinx.cinterop.*

class NativeKVariant private constructor(private val wrapped: JObject) {
    // local caches
    private var _typeOrdinal: Int? = 0

    fun getTypeOrdinal(env: JniEnv): Int {
        if (_typeOrdinal != null) {
            return _typeOrdinal!!
        }
        val cls = NativeKFunc.jclass(env)
        val getClassNameMethod = cls.getMethodID("getTypeOrdinal", "()I")
        val parameterCount = wrapped.callIntMethod(getClassNameMethod)
        return parameterCount.also { _typeOrdinal = it }
    }

    fun toGodot(env: JniEnv): CValue<godot_variant> {
        return converters[getTypeOrdinal(env)].toGodot(env, wrapped)
    }

    fun toJava() = wrapped

    companion object {
        fun fromGodot(env: JniEnv, value: CValue<godot_variant>): NativeKVariant {
            return memScoped {
                val ptr = value.ptr
                val type = nullSafe(Godot.gdnative.godot_variant_get_type)(ptr)
                val ordinal = variantTypeToKVariantTypeOrdinal[type]!!
                NativeKVariant(converters[ordinal].toJava(env, value))
            }
        }

        fun fromJava(wrapped: JObject) = NativeKVariant(wrapped)

        const val SGN = "godot/internal/KVariant"
        fun jclass(env: JniEnv) =  env.currentThread().loadClass("godot.internal.KVariant")

        // must match enum value order in KVariant.Type
        private val converters = arrayOf(
            NilConverter,
            LongConverter,
            DoubleConverter
        )

        // maps to variant type to indexes in converter
        // no, I don't want to follow variant type ordering as its source is
        // outside our control.
        private val variantTypeToKVariantTypeOrdinal = mutableMapOf(
            GODOT_VARIANT_TYPE_NIL to 0,
            GODOT_VARIANT_TYPE_INT to 1,
            GODOT_VARIANT_TYPE_REAL to 2
        )

        private interface Converter {
            fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant>
            fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject
        }

        object NilConverter: Converter {
            override fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant> {
                return cValue {
                    nullSafe(Godot.gdnative.godot_variant_new_nil)(ptr)
                }
            }

            override fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject {
                val cls = jclass(env)
                val fromMethod = cls.getStaticMethodID("nil", "()L$SGN;")
                return checkNotNull(cls.callStaticObjectMethod(fromMethod)) {
                    "Value from java returned null!"
                }
            }

        }

        object LongConverter: Converter {
            override fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant> {
                val cls = jclass(env)
                val asLongMethod = cls.getMethodID("asLong", "()J")
                val value = obj.callLongMethod(asLongMethod)
                return cValue {
                    nullSafe(Godot.gdnative.godot_variant_new_int)(ptr, value)
                }
            }

            override fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject {
                val cls = jclass(env)
                val fromMethod = cls.getStaticMethodID("from", "(J)L$SGN;")
                // returns an int64
                val value = memScoped {
                    nullSafe(Godot.gdnative.godot_variant_as_int)(variant.ptr)
                }
                return checkNotNull(cls.callStaticObjectMethod(fromMethod, value)) {
                    "Value from java returned null!"
                }
            }
        }

        object DoubleConverter: Converter {
            override fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant> {
                val cls = jclass(env)
                val asLongMethod = cls.getMethodID("asDouble", "()D")
                val value = obj.callLongMethod(asLongMethod)
                return cValue {
                    nullSafe(Godot.gdnative.godot_variant_new_int)(ptr, value)
                }
            }

            override fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject {
                val cls = jclass(env)
                val fromMethod = cls.getStaticMethodID("from", "(D)L$SGN;")
                val value = memScoped {
                    nullSafe(Godot.gdnative.godot_variant_as_real)(variant.ptr)
                }
                return checkNotNull(cls.callStaticObjectMethod(fromMethod, value)) {
                    "Value from java returned null!"
                }
            }
        }
    }
}
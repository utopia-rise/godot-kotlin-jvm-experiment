package godot.loader.internal

import godot.gdnative.godot_variant
import godot.gdnative.godot_variant_type.*
import godot.loader.registry.NativeKFunc
import jni.JObject
import jni.JString
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
                val ordinal = checkNotNull(variantTypeToKVariantTypeOrdinal[type]) { "Unable to get ordinal for variant type $type" }
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
            DoubleConverter,
            BoolConverter,
            StringConverter,
            ObjectConverter
        )

        // maps to variant type to indexes in converter
        // no, I don't want to follow variant type ordering as its source is
        // outside our control.
        private val variantTypeToKVariantTypeOrdinal = mutableMapOf(
            GODOT_VARIANT_TYPE_NIL to 0,
            GODOT_VARIANT_TYPE_INT to 1,
            GODOT_VARIANT_TYPE_REAL to 2,
            GODOT_VARIANT_TYPE_BOOL to 3,
            GODOT_VARIANT_TYPE_STRING to 4,
            GODOT_VARIANT_TYPE_OBJECT to 5
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
                val asMethod = cls.getMethodID("asLong", "()J")
                val value = obj.callLongMethod(asMethod)
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
                val asMethod = cls.getMethodID("asDouble", "()D")
                val value = obj.callDoubleMethod(asMethod)
                return cValue {
                    nullSafe(Godot.gdnative.godot_variant_new_real)(ptr, value)
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

        object BoolConverter: Converter {
            override fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant> {
                val cls = jclass(env)
                val asMethod = cls.getMethodID("asBool", "()Z")
                val value = obj.callBoolMethod(asMethod)
                return cValue {
                    nullSafe(Godot.gdnative.godot_variant_new_bool)(ptr, value)
                }
            }

            override fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject {
                val cls = jclass(env)
                val fromMethod = cls.getStaticMethodID("from", "(Z)L$SGN;")
                val value = memScoped {
                    nullSafe(Godot.gdnative.godot_variant_as_bool)(variant.ptr)
                }
                return checkNotNull(cls.callStaticObjectMethod(fromMethod, value)) {
                    "Value from java returned null!"
                }
            }
        }

        object StringConverter: Converter {
            override fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant> {
                val cls = jclass(env)
                val asMethod = cls.getMethodID("asString", "()Ljava/lang/String;")
                val value = obj.callObjectMethod(asMethod)
                checkNotNull(value) { "KVariant.asString returned null!" }
                return cValue {
                    val ktStr = JString.unsafeCast(value).toKString()
                    ktStr.asGDString {
                        nullSafe(Godot.gdnative.godot_variant_new_string)(this@cValue.ptr, it.value.ptr)
                    }
                }
            }

            override fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject {
                val cls = jclass(env)
                val fromMethod = cls.getStaticMethodID("from", "(Ljava/lang/String;)L$SGN;")
                val value = memScoped {
                    nullSafe(Godot.gdnative.godot_variant_as_string)(variant.ptr)
                }
                // we are passing this value to java, no need to create a global ref
                val jstr = env.newString(GdString(value).toKString())
                return checkNotNull(cls.callStaticObjectMethod(fromMethod, jstr)) {
                    "Value from java returned null!"
                }
            }
        }

        object ObjectConverter: Converter {
            override fun toGodot(env: JniEnv, obj: JObject): CValue<godot_variant> {
                val cls = jclass(env)
                val asMethod = cls.getMethodID("asObject", "()L${NativeKObject.SGN};")
                val value = obj.callObjectMethod(asMethod)
                return cValue {
                    if (value != null) {
                        val nativeKObject = NativeKObject(value)
                        nullSafe(Godot.gdnative.godot_variant_new_object)(ptr, nativeKObject.getRawPtr(env))
                        // dispose our reference here, otherwise it will leak!
                        nativeKObject.dispose()
                    } else {
                        nullSafe(Godot.gdnative.godot_variant_new_nil)(ptr)
                    }
                }
            }

            override fun toJava(env: JniEnv, variant: CValue<godot_variant>): JObject {
                val cls = jclass(env)
                val fromMethod = cls.getStaticMethodID("fromRawPtr", "(J)L$SGN;")
                val value = memScoped {
                    nullSafe(Godot.gdnative.godot_variant_as_object)(variant.ptr)
                }
                return checkNotNull(cls.callStaticObjectMethod(fromMethod, value.toLong())) {
                    "Value from java returned null!"
                }
            }
        }
    }
}
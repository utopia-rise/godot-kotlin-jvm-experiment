package jni

import jni.sys.*
import kotlin.reflect.KMutableProperty1

sealed class JValue<T> (var value: T, val setter: KMutableProperty1<jvalue, T>) {
    class Byte(value: jbyte): JValue<jbyte>(value, jvalue::b)
    class Char(value: jchar): JValue<jchar>(value, jvalue::c)
    class Double(value: jdouble): JValue<jdouble>(value, jvalue::d)
    class Float(value: jfloat): JValue<jfloat>(value, jvalue::f)
    class Int(value: jint): JValue<jint>(value,  jvalue::i)
    class Long(value: jlong): JValue<jlong>(value, jvalue::j)
    class Object(value: jobject?): JValue<jobject?>(value, jvalue::l)
    class Short(value: jshort): JValue<jshort>(value, jvalue::s)
    class Boolean(value: jboolean): JValue<jboolean>(value, jvalue::z)

    fun set(jvalue: jvalue) {
        setter.set(jvalue, value)
    }
}
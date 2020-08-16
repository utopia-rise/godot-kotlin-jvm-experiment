package jni

import jni.sys.*
import kotlinx.cinterop.*
import kotlin.reflect.KMutableProperty1

typealias InvokeFn = JNIInvokeInterface_
typealias EnvFn = JNINativeInterface_

operator fun <F: CFunction<*>, T: CPointer<F>> JavaVMVar.get(property: KMutableProperty1<InvokeFn, T?>): T {
    return property.get(pointed!!)!!
}

operator fun <F: CFunction<*>, T: CPointer<F>> JNIEnvVar.get(property: KMutableProperty1<EnvFn, T?>): T {
    return property.get(pointed!!)!!
}

fun <T> MemScope.convertToJValueArgs(args: Array<T>): CArrayPointer<jvalue> {
    return allocArray<jvalue>(args.size).also {
        args.forEachIndexed { i, v -> it[i].setFrom(v) }
    }
}
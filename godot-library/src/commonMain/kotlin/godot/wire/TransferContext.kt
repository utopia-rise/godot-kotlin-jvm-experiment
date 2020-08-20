package godot.wire

import godot.internal.VoidPtr

expect class TransferContext() {
    fun writeArguments(vararg values: TValue): Boolean
    fun readArguments(): List<TValue>

    fun writeReturnValue(value: TValue): Boolean
    fun readReturnValue(): TValue

    fun callMethod(ptr: VoidPtr, className: String, method: String, expectedReturnType: TValue.Type)
}
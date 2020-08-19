package godot.wire

expect class TransferContext() {
    fun writeArguments(vararg values: TValue): Boolean
    fun readArguments(): List<TValue>

    fun writeReturnValue(value: TValue): Boolean
    fun readReturnValue(): TValue
}
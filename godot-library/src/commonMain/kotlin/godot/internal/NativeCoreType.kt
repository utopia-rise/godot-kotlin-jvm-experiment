package godot.internal

abstract class NativeCoreType: CoreType {
    @Suppress("LeakingThis")
    val ptr: VoidPtr = __new()

    protected abstract fun __new(): VoidPtr;
}
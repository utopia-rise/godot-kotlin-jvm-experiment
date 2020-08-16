package godot.internal

abstract class KObject {
    internal var ptr: VoidPtr = nullptr

    open fun _onInit() = Unit
    open fun _onDestroy() = Unit
}
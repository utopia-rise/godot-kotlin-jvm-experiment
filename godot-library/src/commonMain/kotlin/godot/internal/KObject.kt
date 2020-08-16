package godot.internal

@Suppress("LeakingThis")
abstract class KObject {
    internal var ptr: VoidPtr = nullptr

    init {
        if (BindingContext.shouldInitPtr()) {
            ptr = __new()
        }
    }

    open fun _onInit() = Unit
    open fun _onDestroy() = Unit

    open fun __new(): VoidPtr {
        TODO("Please override __new method!")
    }
}
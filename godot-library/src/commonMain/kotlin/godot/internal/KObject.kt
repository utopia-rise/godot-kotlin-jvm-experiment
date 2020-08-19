package godot.internal

import godot.internal.meta.JniExposed

@Suppress("LeakingThis")
open class KObject {
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

    @JniExposed
    fun _getRawPtr() = ptr

    override fun hashCode(): Int {
        return ptr.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KObject) {
            return false
        }
        return other.ptr == ptr
    }
}
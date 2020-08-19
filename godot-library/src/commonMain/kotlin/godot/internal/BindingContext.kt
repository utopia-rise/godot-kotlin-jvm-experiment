package godot.internal

import godot.wire.TransferContext

object BindingContext {
    val transferContext = TransferContext()

    private var shouldInit = true
    /**
     * Check if the we should initialized the ptr to an object. It also reverts the value of shouldInit
     * to true. This method is used in conjunction with [instantiateWith] to provide us a mechanism to provide
     * our own ptr when instantiating an object.
     */
    fun shouldInitPtr(): Boolean {
        val current = shouldInit
        shouldInit = true
        return current
    }

    fun <T: KObject> instantiateWith(ptr: VoidPtr, constructor: () -> T): T {
        shouldInit = false
        val instance = constructor()
        instance.ptr = ptr
        return instance
    }
}
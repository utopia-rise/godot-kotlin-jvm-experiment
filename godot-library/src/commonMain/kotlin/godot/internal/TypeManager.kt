package godot.internal

object TypeManager {
    fun registerUserType(className: String) {}

    fun wrap(ptr: VoidPtr): KObject {
        // TODO: check for null? should return null object or throw exception?
        // TODO wrap to appropriate type!
        return BindingContext.instantiateWith(ptr, ::KObject)
    }
}
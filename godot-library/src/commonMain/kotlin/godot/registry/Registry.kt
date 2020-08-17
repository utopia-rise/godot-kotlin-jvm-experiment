package godot.registry

import godot.internal.KObject

class Registry {
    private val handles = mutableListOf<ClassHandle<*>>()
    fun <T: KObject> registerClass(className: String, superClass: String, factory: () -> T) {
        val handle = ClassHandle(className, superClass, factory)
        handles.add(handle)
    }

    internal fun getAllClasses() = handles.toList()
}
package godot.registry

import godot.internal.KObject

class ClassHandle<T: KObject>(val className: String,
                              val superClass: String,
                              val constructor: Constructor<T>) {
}
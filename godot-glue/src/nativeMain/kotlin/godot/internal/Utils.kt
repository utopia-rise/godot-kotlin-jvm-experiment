package godot.internal

inline fun <reified T: Any> nullSafe(obj: T?) : T {
    return obj!!
}
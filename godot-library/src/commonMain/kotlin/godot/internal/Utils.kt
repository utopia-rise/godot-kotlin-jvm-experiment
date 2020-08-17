package godot.internal

typealias VoidPtr = Long
const val nullptr: VoidPtr = 0L

fun String.camelToSnakeCase(): String {
    return "(?<=[a-zA-Z])[A-Z]".toRegex().replace(this) {
        "_${it.value}"
    }.toLowerCase()
}

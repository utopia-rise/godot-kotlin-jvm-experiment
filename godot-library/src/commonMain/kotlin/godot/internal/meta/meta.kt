package godot.internal.meta


/**
 * This annotation's only purpose is for documentation.
 *
 * Any function, field or property annotated with this annotation is available JNI.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
@Retention(AnnotationRetention.SOURCE)
annotation class JniExposed
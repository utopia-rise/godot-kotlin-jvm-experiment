package godot.loader.registry

import godot.gdnative.godot_variant
import godot.loader.internal.NativeBindingContext
import godot.loader.internal.NativeKObject
import godot.loader.internal.NativeKVariant
import godot.loader.internal.nullSafe
import kotlinx.cinterop.*

fun createInstance(instance: COpaquePointer?, methodData: COpaquePointer?): COpaquePointer? {
    return NativeBindingContext.bindScope {
        val classHandle = nullSafe(methodData).asStableRef<NativeClassHandle>()
            .get()
        val kotlinInstance = classHandle.wrap(this, nullSafe(instance))
        kotlinInstance._onInit(this)
        val stableRef = StableRef.create(kotlinInstance)
        stableRef.asCPointer()
    }
}

fun disposeClassHandle(ref: COpaquePointer?) {
    NativeBindingContext.bindScope {
        val handle = nullSafe(ref).asStableRef<NativeClassHandle>()
        handle.get().dispose()
        handle.dispose()
    }
}

fun destroyInstance(instance: COpaquePointer?, methodData: COpaquePointer?, classData: COpaquePointer?) {
    NativeBindingContext.bindScope {
        val kotlinInstanceRef = nullSafe(classData).asStableRef<NativeKObject>()
        val kotlinInstance = kotlinInstanceRef.get()
        kotlinInstance._onDestroy(this)
        kotlinInstanceRef.dispose()
    }
}

fun invokeMethod(
    instance: COpaquePointer?,
    methodData: COpaquePointer?,
    classData: COpaquePointer?,
    numArgs: Int,
    args: CPointer<CPointerVar<godot_variant>>?
): CValue<godot_variant> {
    return NativeBindingContext.bindScope {
        val kotlinInstanceRef = nullSafe(classData).asStableRef<NativeKObject>()
        val kotlinInstance = kotlinInstanceRef.get()
        val funcHandleRef = nullSafe(methodData).asStableRef<NativeKFunc>()
        val funcHandle = funcHandleRef.get()

        val parameterCount  = funcHandle.getParameterCount(this)
        check(parameterCount == numArgs) {
            "Invalid number of arguments, $numArgs passed but $parameterCount expected."
        }

        val variantArgs = if (numArgs == 0) {
            emptyArray()
        } else {
            requireNotNull(args) { "args is null!" }
            Array(numArgs) { i -> NativeKVariant.fromGodot(this, args[i]!!.pointed.readValue())}
        }

        funcHandle(this, kotlinInstance, variantArgs).toGodot(this)
    }

}
package godot.loader.registry

import godot.loader.internal.NativeKObject
import godot.loader.internal.NativeBindingContext
import godot.loader.internal.nullSafe
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef

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
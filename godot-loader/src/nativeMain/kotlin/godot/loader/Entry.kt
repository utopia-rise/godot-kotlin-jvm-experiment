package godot.loader

import godot.gdnative.godot_gdnative_init_options
import godot.gdnative.godot_gdnative_terminate_options
import godot.loader.internal.Godot
import godot.loader.internal.Loader
import kotlinx.cinterop.COpaquePointer

@CName("godot_gdnative_init")
fun godot_gdnative_init(options: godot_gdnative_init_options) {
    Godot.init(options)
}

@CName("godot_gdnative_terminate")
fun godot_gdnative_terminate(options: godot_gdnative_terminate_options) {
    Godot.terminate(options)
}

@CName("godot_nativescript_init")
fun godot_nativescript_init(handle: COpaquePointer) {
    Godot.nativescriptInit(handle)
}

@CName("godot_nativescript_terminate")
fun godot_nativescript_terminate(handle: COpaquePointer) {
    Godot.nativescriptTerminate(handle)
}
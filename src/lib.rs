mod godot;

use gdnative::*;
use std::os::raw::c_void;

#[no_mangle]
fn godot_gdnative_init(options: *const godot_gdnative_init_options) {
    godot::init(options)
}

#[no_mangle]
fn godot_gdnative_terminate(options: *const godot_gdnative_terminate_options) {
    godot::terminate(options)
}

#[no_mangle]
fn godot_nativescript_init(handle: *const c_void) {
    godot::nativescript_init(handle)
}

#[no_mangle]
fn godot_nativescript_destroy(handle: *const c_void) {
    godot::nativescript_destroy(handle)
}
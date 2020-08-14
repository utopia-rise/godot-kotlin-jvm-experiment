use gdnative::*;
use std::ptr;
use std::ffi::c_void;
use std::ptr::null;

struct GodotState {
    gdnative: *const godot_gdnative_core_api_struct
}

static mut GODOT_STATE: GodotState = GodotState {
    gdnative: null()
};

pub struct Godot;

pub fn init(options: *const godot_gdnative_init_options) {
    unsafe {
        GODOT_STATE.gdnative = (*options).api_struct;
    }
    println!("GDScript Initialized!")
}

pub fn nativescript_init(handle: *const c_void) {

}

pub fn nativescript_destroy(handle: *const c_void) {

}

pub fn terminate(options: *const godot_gdnative_terminate_options) {

}
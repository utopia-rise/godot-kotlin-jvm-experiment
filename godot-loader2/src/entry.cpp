#include <gdnative_api_struct.gen.h>
#include "godot.h"

extern "C" void godot_gdnative_init(godot_gdnative_init_options* options) {
    Godot::instance().init(options);
}

extern "C" void godot_gdnative_terminate(godot_gdnative_terminate_options* options) {
    Godot::instance().terminate(options);
}

extern "C" void godot_nativescript_init(void* handle) {
    Godot::instance().nativescriptInit(handle);
}

extern "C" void godot_nativescript_terminate(void* handle) {
    Godot::instance().nativescriptTerminate(handle);
}
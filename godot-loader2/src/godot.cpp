#include "godot.h"
#include <iostream>

Godot& Godot::instance() {
    static Godot instance;
    return instance;
}

void Godot::init(godot_gdnative_init_options* options) {
    gd = options->api_struct;
    gd11 = (const godot_gdnative_core_1_1_api_struct*) gd->next;
    gd12 = (const godot_gdnative_core_1_2_api_struct*) gd11->next;

    for (auto i = 0; i < gd->num_extensions; i++) {
        switch (gd->extensions[i]->type) {
            case GDNATIVE_EXT_NATIVESCRIPT:
                ns = (const godot_gdnative_ext_nativescript_api_struct*) gd->extensions[i];
                break;
        }
    }

    ns11 = (const godot_gdnative_ext_nativescript_1_1_api_struct*) ns->next;
    auto& bindingContext = NativeBindingContext::instance();
    auto libraryPath = fromGDString(options->active_library_path);
    bindingContext.bind(
            options->gd_native_library,
            libraryPath
    );
}

void Godot::terminate(godot_gdnative_terminate_options* options) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.unbind(!options->in_editor);
}

void Godot::nativescriptInit(void* handle) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.registerClasses(handle);
}

void Godot::nativescriptTerminate(void* handle) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.unRegisterClasses(handle);
}

std::string Godot::fromGDString(const godot_string* str) {
    auto wstr = std::wstring(gd->godot_string_wide_str(str));
    return std::string(wstr.begin(), wstr.end());
}


#include "godot.h"
#include <iostream>

Godot &Godot::instance() {
    static Godot instance;
    return instance;
}

void Godot::init(godot_gdnative_init_options* options) {
    std::cout << "Hello World!" << std::endl;
    gd = options->api_struct;
    gd11 = (const godot_gdnative_core_1_1_api_struct*) gd->next;
    gd12 = (const godot_gdnative_core_1_2_api_struct*) gd11->next;

    for (auto i = 0; i < gd->num_extensions; i++) {
        switch (gd->extensions[i]->type) {
            case GDNATIVE_EXT_NATIVESCRIPT:
                ns = (const godot_gdnative_ext_nativescript_api_struct* )gd->extensions[i];
                break;
        }
    }

    ns11 = (const godot_gdnative_ext_nativescript_1_1_api_struct*) ns->next;
    auto& bindingContext = NativeBindingContext::instance();
    auto libraryPath = fromGDString(options->active_library_path);
    bindingContext.bind(
            options->gd_native_library,
            std::string(libraryPath.begin(), libraryPath.end())
    );
}

void Godot::terminate(godot_gdnative_terminate_options* options) {
    auto& bindingContext = NativeBindingContext::instance();
    if (!options->in_editor) {
        bindingContext.unbind();
    }
}

void Godot::nativescriptTerminate(void *handle) {

}

void Godot::nativescriptInit(void *handle) {
}

std::wstring Godot::fromGDString(const godot_string *str) {
    return std::wstring(gd->godot_string_wide_str(str));
}


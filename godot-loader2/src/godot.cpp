#include "godot.h"
#include <iostream>

Godot &Godot::instance() {
    static Godot instance;
    return instance;
}

void Godot::init(godot_gdnative_init_options *options) {
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

    NativeBindingContext::instance().bind();
}

void Godot::terminate(godot_gdnative_terminate_options *options) {
    if (!options->in_editor) {
        NativeBindingContext::instance().unbind();
    }
}

void Godot::nativescriptTerminate(void *handle) {

}

void Godot::nativescriptInit(void *handle) {
}


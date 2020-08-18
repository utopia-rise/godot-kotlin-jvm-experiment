#ifndef GODOT_LOADER_GODOT_H
#define GODOT_LOADER_GODOT_H
#include <gdnative_api_struct.gen.h>
#include "native_binding_context.h"

class Godot {
public:
    Godot(const Godot&) = delete;
    void operator=(const Godot&) = delete;

    static Godot& instance();

    void init(godot_gdnative_init_options* options);
    void terminate(godot_gdnative_terminate_options* options);
    void nativescriptInit(void* handle);
    void nativescriptTerminate(void* handle);

    std::wstring fromGDString(const godot_string* str);

private:
    const godot_gdnative_core_api_struct* gd;
    const godot_gdnative_core_1_1_api_struct* gd11;
    const godot_gdnative_core_1_2_api_struct* gd12;
    const godot_gdnative_ext_nativescript_api_struct* ns;
    const godot_gdnative_ext_nativescript_1_1_api_struct* ns11;

    Godot() = default;
};

#endif //GODOT_LOADER_GODOT_H

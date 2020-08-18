#ifndef GODOT_LOADER_NATIVEBINDINGCONTEXT_H
#define GODOT_LOADER_NATIVEBINDINGCONTEXT_H
#include "jvm.h"

class NativeBindingContext {
public:
    NativeBindingContext(const NativeBindingContext&) = delete;
    void operator=(const NativeBindingContext&) = delete;

    static NativeBindingContext& instance();

    void bind();
    void unbind();
private:
    NativeBindingContext() = default;
};


#endif //GODOT_LOADER_NATIVEBINDINGCONTEXT_H

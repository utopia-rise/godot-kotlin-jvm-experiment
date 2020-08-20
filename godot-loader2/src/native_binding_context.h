#ifndef GODOT_LOADER_NATIVEBINDINGCONTEXT_H
#define GODOT_LOADER_NATIVEBINDINGCONTEXT_H
#include <jvm.h>
#include <gdnative_api_struct.gen.h>
#include "native_class_handle.h"
#include "native_transfer_context.h"
#include "jni_utils.h"

class NativeBindingContext {
public:
    NativeBindingContext(const NativeBindingContext&) = delete;
    void operator=(const NativeBindingContext&) = delete;

    static NativeBindingContext& instance();

    void bind(godot_object* library, const std::string& libraryPath);
    void unbind(bool destroyJvm);

    void startScope();
    void endScope();

    void registerClasses(void* nativescriptHandle);
    void unRegisterClasses(void* nativescriptHandle);

    jni::JObject classLoader;

    NativeTransferContext transferContext;

    static JClassHelper JH;
private:
    jni::JObject wrapped;
    NativeBindingContext() = default;

    godot_object* library = nullptr;
    std::string libraryPath;
    std::string projectDir;
};


#endif //GODOT_LOADER_NATIVEBINDINGCONTEXT_H

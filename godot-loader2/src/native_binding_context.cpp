#include <iostream>
#include "native_binding_context.h"

NativeBindingContext& NativeBindingContext::instance() {
    static NativeBindingContext bindingContext;
    return bindingContext;
}

void NativeBindingContext::bind(godot_object *library, const std::string& libraryPath) {
    this->library = library;
    this->libraryPath = libraryPath;
    this->projectDir = libraryPath.substr(0, libraryPath.find_last_of('/') + 1);
    std::cout << "project_dir: " << this->projectDir << std::endl;
    auto args = jni::InitArgs();
    args.option("-Xcheck:jni");
    args.option("-verbose:jni");
    jni::Jvm::init(args);

    startScope();
    // set class loader here
    endScope();
}

void NativeBindingContext::unbind() {
    this->library = nullptr;
    jni::Jvm::destroy();
}

void NativeBindingContext::startScope() {
    auto env = jni::Jvm::currentEnv();
    env.pushLocalFrame(100);
}

void NativeBindingContext::endScope() {
    auto env = jni::Jvm::currentEnv();
    env.popLocalFrame()
}

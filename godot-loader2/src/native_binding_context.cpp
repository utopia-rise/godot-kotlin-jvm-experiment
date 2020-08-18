#include "native_binding_context.h"

NativeBindingContext &NativeBindingContext::instance() {
    static NativeBindingContext bindingContext;
    return bindingContext;
}

void NativeBindingContext::bind() {
    auto args = jni::InitArgs();
    args.option("-Xcheck:jni");
    args.option("-verbose:jni");
    jni::Jvm::init(args);
}

void NativeBindingContext::unbind() {
    jni::Jvm::destroy();
}


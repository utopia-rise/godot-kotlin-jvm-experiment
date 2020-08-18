#include "bridge.h"
#include "native_binding_context.h"
#include <jvm.h>

void* createInstance(void* instance, void* methodData) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto env = jni::Jvm::currentEnv();
    auto handle = (NativeClassHandle*) methodData;
    auto kotlinInstance = handle->wrap(env, bindingContext.classLoader, instance);
    kotlinInstance->_onInit(env, bindingContext.classLoader);
    bindingContext.endScope();
    return kotlinInstance;
}

void disposeClassHandle(void* ref) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto env = jni::Jvm::currentEnv();
    auto handle = (NativeClassHandle*) ref;
    handle->dispose(env);
    delete handle;
    bindingContext.endScope();
}

void destroyInstance(void* instance, void* methodData, void* classData) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto env = jni::Jvm::currentEnv();
    auto kotlinInstance = (NativeKObject*) classData;
    kotlinInstance->_onDestroy(env, bindingContext.classLoader);
    kotlinInstance->dispose(env);
    delete kotlinInstance;
    bindingContext.endScope();
}

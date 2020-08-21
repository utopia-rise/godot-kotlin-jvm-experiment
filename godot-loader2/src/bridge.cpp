#include "bridge.h"
#include "native_binding_context.h"
#include <jvm.h>

void* createInstance(void* instance, void* methodData) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto& env = jni::Jvm::currentEnv();
    auto handle = (NativeClassHandle*) methodData;
    auto& classLoader = bindingContext.getClassLoader();
    auto kotlinInstance = handle->wrap(env, classLoader, instance);
    kotlinInstance->_onInit(env, classLoader);
    bindingContext.endScope();
    return kotlinInstance;
}

void disposeClassHandle(void* ref) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto& env = jni::Jvm::currentEnv();
    auto handle = (NativeClassHandle*) ref;
    handle->dispose(env);
    delete handle;
    bindingContext.endScope();
}

void destroyInstance(void* instance, void* methodData, void* classData) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto& env = jni::Jvm::currentEnv();
    auto kotlinInstance = (NativeKObject*) classData;
    auto& classLoader = bindingContext.getClassLoader();
    kotlinInstance->_onDestroy(env, classLoader);
    kotlinInstance->dispose(env);
    delete kotlinInstance;
    bindingContext.endScope();
}

void disposeFuncHandle(void* ref) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto& env = jni::Jvm::currentEnv();
    auto handle = (NativeKFunction*) ref;
    handle->dispose(env);
    delete handle;
    bindingContext.endScope();
}

godot_variant invokeMethod(void* instance, void* methodData, void* classData, int numArgs, godot_variant** args) {
    auto& bindingContext = NativeBindingContext::instance();
    bindingContext.startScope();
    auto& env = jni::Jvm::currentEnv();
    auto kotlinInstance = (NativeKObject*) classData;
    auto handle = (NativeKFunction*) methodData;

    auto parameterCount = handle->getParameterCount(env, bindingContext.getClassLoader());
    if (parameterCount != numArgs) {
        std::stringstream ss;
        ss << "Invalid number of args, expecting " << parameterCount << " but received " << numArgs;
        throw std::runtime_error(ss.str());
    }

    auto convertedArgs = std::vector<NativeTValue>();
    convertedArgs.reserve(numArgs);
    for (auto i = 0; i < numArgs; i++) {
        convertedArgs.emplace_back(NativeTValue(*args[i]));
    }

    auto res = handle->invoke(env, bindingContext.getClassLoader(), kotlinInstance, convertedArgs).toGVariant();
    bindingContext.endScope();
    return res;
}
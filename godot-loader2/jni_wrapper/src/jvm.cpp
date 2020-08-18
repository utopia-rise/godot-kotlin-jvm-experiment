#include <cassert>
#include "jvm.h"

namespace jni {
    JavaVM* Jvm::vm = nullptr;
    Env Jvm::env = Env(nullptr);

    void Jvm::init(const InitArgs& initArgs) {
        auto res = getExisting();
        if (res == nullptr) {
            res = create(initArgs);
        }
        assert(res != nullptr);
        vm = res;
        version = initArgs.version;
    }

    void Jvm::destroy() {
        vm->DetachCurrentThread();
        vm->DestroyJavaVM();
    }

    JavaVM *Jvm::create(const InitArgs& initArgs) {
        auto nOptions = initArgs.options.size();
        JavaVMOption options[nOptions];
        JavaVMInitArgs args;
        args.version = initArgs.version;
        args.nOptions = nOptions;
        args.options = options;

        for (auto i = 0; i < nOptions; i++) {
            args.options[i].optionString = (char* ) initArgs.options[i].c_str();
        }

        JavaVM* vm;
        JNIEnv* env;
        auto result = JNI_CreateJavaVM(&vm, (void**) &env, (void*) &args);
        if (result != JNI_OK) {
            throw JniError("Failed to create a new vm!");
        }
        return vm;
    }

    JavaVM* Jvm::getExisting() {
        JavaVM* buffer[1];
        int count;
        auto result = JNI_GetCreatedJavaVMs(buffer, 1, &count);
        if (result != JNI_OK) {
            throw JniError("Failed to retrieve existing vm!");
        }
        if (count > 0) {
            return buffer[0];
        }
        return nullptr;
    }

    Env &Jvm::attach() {
        if (Jvm::env.isValid()) {
            return Jvm::env;
        }
        JNIEnv* env;
        auto result = vm->GetEnv((void**) &env, version);
        if (result == JNI_EDETACHED) {
            result = vm->AttachCurrentThread((void**) &env, nullptr);
            if (result != JNI_OK) {
                throw JniError("Failed to attach vm to current thread!");
            }
        }
        Jvm::env = Env(env);
        return Jvm::env;
    }

    void Jvm::detach() {
        auto result = vm->DetachCurrentThread();
        if (result != JNI_OK) {
            throw JniError("Failed to detach vm to current thread!");
        }
        Jvm::env = Env(nullptr);
    }

    Env &Jvm::currentEnv() {
        return attach();
    }
}
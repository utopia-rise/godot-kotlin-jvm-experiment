#include <cassert>
#include "jvm.h"

namespace jni {
    JavaVM* Jvm::vm = nullptr;

    void Jvm::init(const InitArgs& initArgs) {
        auto res = getExisting();
        if (res == nullptr) {
            res = create(initArgs);
        }
        assert(res != nullptr);
        vm = res;
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

        JavaVM* res;
        JNIEnv* env;
        auto result = JNI_CreateJavaVM(&res, (void**) &env, (void*) &args);
        assert(result == JNI_OK);
        return res;
    }

    JavaVM* Jvm::getExisting() {
        JavaVM* buffer[1];
        int count;
        auto result = JNI_GetCreatedJavaVMs(buffer, 1, &count);
        assert(result == JNI_OK);
        if (count > 0) {
            return buffer[0];
        }
        return nullptr;
    }
}
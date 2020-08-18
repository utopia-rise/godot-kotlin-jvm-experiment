#include "env.h"

namespace jni {
    Env::Env(JNIEnv* env) {
        this->env = env;
    }

    void Env::pushLocalFrame(int capacity) {
        auto result = env->PushLocalFrame(capacity);
        if (result != JNI_OK) {
            throw JniError("Failed to push local frame!");
        }
    }

    void Env::popLocalFrame() {
        env->PopLocalFrame(nullptr);
    }

    bool Env::isValid() {
        return env != nullptr;
    }
}
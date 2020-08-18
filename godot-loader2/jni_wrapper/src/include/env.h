#ifndef GODOT_LOADER_ENV_H
#define GODOT_LOADER_ENV_H
#include "jni.h"
#include "exceptions.h"

namespace jni {
    class Env {
    public:
        Env(JNIEnv*);
        Env(const Env&) = default;
        Env& operator=(const Env&) = default;

        void pushLocalFrame(int capacity);
        void popLocalFrame();

        bool isValid();
    private:
        JNIEnv* env;
    };
}


#endif //GODOT_LOADER_ENV_H

#ifndef GODOT_LOADER_JVM_H
#define GODOT_LOADER_JVM_H
#include <jni.h>
#include "init_args.h"

namespace jni {
    class Jvm {
    public:
        Jvm(const Jvm&) = delete;
        void operator=(const Jvm&) = delete;

        static void init(const InitArgs&);
        static void destroy();

    private:
        Jvm() = default;
        static JavaVM* vm;

        static JavaVM* create(const InitArgs&);
        static JavaVM* getExisting();
    };
}

#endif //GODOT_LOADER_JVM_H

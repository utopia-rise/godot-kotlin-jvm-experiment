#ifndef GODOT_LOADER_EXCEPTIONS_H
#define GODOT_LOADER_EXCEPTIONS_H

#include <stdexcept>

namespace jni {
    class JniError : public std::runtime_error {
    public:
        JniError(const char *msg) : runtime_error(msg) {}
    };
}

#endif //GODOT_LOADER_EXCEPTIONS_H

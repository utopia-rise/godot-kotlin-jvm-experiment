#ifndef GODOT_LOADER_ENV_H
#define GODOT_LOADER_ENV_H
#include <jni.h>
#include "types.h"
#include "exceptions.h"

namespace jni {
    class Env {
    public:
        Env(JNIEnv*);
        Env(const Env&) = default;
        Env& operator=(const Env&) = default;

        void pushLocalFrame(int capacity);
        void popLocalFrame();

        JClass findClass(const char* name);

        JObject newString(const char* str);
        std::string fromJString(jni::JString str);

        bool exceptionCheck();
        void exceptionDescribe();
        void exceptionClear();

        void checkExceptions();

        void* getDirectBufferAddress(const jni::JObject& buffer);
        int getDirectBufferCapacity(const jni::JObject& buffer);

        bool isValid();
    private:
        JNIEnv* env;

        friend class JObject;
        friend class JClass;
        friend class JArray;
        friend class JObjectArray;
    };
}


#endif //GODOT_LOADER_ENV_H

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

    JClass Env::findClass(const char *name) {
        auto cls = env->FindClass(name);
        if (cls == nullptr) {
            throw ClassNotFoundError(name);
        }
        return {cls};
    }

    JObject Env::newString(const char *str) {
        auto jstr = env->NewStringUTF(str);
        checkExceptions();
        return {jstr};
    }

    bool Env::exceptionCheck() {
        return env->ExceptionCheck();
    }

    void Env::exceptionDescribe() {
        env->ExceptionDescribe();
    }

    void Env::exceptionClear() {
        env->ExceptionClear();
    }

    void Env::checkExceptions() {
        if (exceptionCheck()) {
            exceptionDescribe();
            exceptionClear();
            throw JniError("An exception has occurred!");
        }
    }

    std::string Env::fromJString(jni::JString str) {
        auto jstr = (jstring) str.obj;
        auto utfString = env->GetStringUTFChars(jstr, NULL);
        auto ret = std::string(utfString);
        checkExceptions();
        env->ReleaseStringUTFChars(jstr, utfString);
        return ret;
    }
}
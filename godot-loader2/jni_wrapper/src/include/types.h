#ifndef GODOT_LOADER_JOBJECT_H
#define GODOT_LOADER_JOBJECT_H
#include <jni.h>
#include <initializer_list>


namespace jni {
    // forward declare
    class Env;
    class JValue;

    typedef jmethodID MethodId;
    typedef jfieldID FieldId;

    class JObject {
    public:
        jobject obj;
        JObject(jobject);
        JObject() : JObject(nullptr) {}

        JObject newGlobalRef(Env& env);
        template <class T>
        __always_inline T newGlobalRef(Env& env) {
            return {newGlobalRef(env).obj};
        }
        void deleteGlobalRef(Env& env);

        JObject callObjectMethod(Env& env, MethodId method, std::initializer_list<JValue> values = {});

        bool isNull ();
    };

    class JString : public JObject {
    public:
        JString(jstring str) : JObject(str) {}
    };

    class JArray : public JObject {
    public:
        JArray(jarray array) : JObject(array) {}

        int length(Env& env);
    };

    class JObjectArray : public JArray {
    public:
        JObjectArray(jarray array) : JArray(array) {}

        void set(Env& env, int index, JObject value);
        JObject get(Env& env, int index);
    };


    class JClass : public JObject {
    public:
        JClass(jclass cls) : JObject(cls) {}

        JObject newInstance(Env& env, MethodId ctor, std::initializer_list<JValue> values = {});
        JObjectArray newObjectArray(Env& env, int size, JObject initial = {});
        MethodId getConstructorMethodId(Env& env, const char* signature);
        MethodId getMethodId(Env& env, const char* name, const char* signature);
        MethodId getStaticMethodId(Env& env, const char* name, const char* signature);

        JObject callStaticObjectMethod(Env& env, MethodId method, std::initializer_list<JValue> values = {});
    };

    class JValue {
    public:
        jvalue value;

        JValue(JObject& obj) {
            value.l = obj.obj;
        }

        JValue(jint i) {
            value.i = i;
        }

        JValue(jlong i) {
            value.i = i;
        }

        JValue(jboolean b) {
            value.z = b;
        }

        JValue(jfloat f) {
            value.f = f;
        }

        JValue(jdouble d) {
            value.d = d;
        }

        JValue(jbyte b) {
            value.b = b;
        }

        JValue(jchar c) {
            value.c = c;
        }

        JValue(jshort s) {
            value.s = s;
        }
    };

#define unpack_args(args) \
    auto args = std::vector<jvalue>(); for (auto value : values) {  args.emplace_back(value.value); }
}


#endif //GODOT_LOADER_JOBJECT_H

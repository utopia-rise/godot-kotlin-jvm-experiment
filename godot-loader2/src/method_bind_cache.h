#ifndef GODOT_LOADER_METHOD_BIND_CACHE_H
#define GODOT_LOADER_METHOD_BIND_CACHE_H
#include <gdnative_api_struct.gen.h>
#include <map>
#include <string>

class MethodBindCache {
public:
    static godot_method_bind* get(const char* className, const char* method);
    static void reset();

private:
    static std::map<std::string, godot_method_bind*> CACHE;
};


#endif //GODOT_LOADER_METHOD_BIND_CACHE_H

#include "method_bind_cache.h"
#include "godot.h"

std::map<std::string, godot_method_bind*> MethodBindCache::CACHE = {};

godot_method_bind* MethodBindCache::get(const char* className, const char* method) {
    auto key = std::string();
    key.append(className);
    key.append(":");
    key.append(method);
    if (CACHE.find(key) != CACHE.end()) {
        return CACHE.at(key);
    }
    auto& gd = Godot::instance();
    auto mb = gd.gd->godot_method_bind_get_method(className, method);
    assert(mb != nullptr);
    CACHE[key] = mb;
    return mb;
}

void MethodBindCache::reset() {
    CACHE.clear();
}

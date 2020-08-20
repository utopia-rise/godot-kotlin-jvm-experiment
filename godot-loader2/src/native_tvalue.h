#ifndef GODOT_LOADER_NATIVE_TVALUE_H
#define GODOT_LOADER_NATIVE_TVALUE_H
#include <gdnative_api_struct.gen.h>
#include "wire.pb.h"

class NativeTValue {
public:
    KVariant data;
    NativeTValue(KVariant data);
    ~NativeTValue();
    explicit NativeTValue(godot_variant variant);
    NativeTValue(const NativeTValue&) = default;
    NativeTValue& operator=(const NativeTValue&) = default;

    godot_variant  toGVariant();
};


#endif //GODOT_LOADER_NATIVE_TVALUE_H

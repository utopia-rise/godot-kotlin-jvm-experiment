#ifndef GODOT_LOADER_ICALL_ARGS_H
#define GODOT_LOADER_ICALL_ARGS_H
#include "godot.h"
#include "wire.pb.h"
#include <vector>
#include "layouts.h"

struct ICallValue {
    KVariant::TypeCase type;
    union {
        void* ptrValue;
        long longValue;
        double realValue;
        godot_string stringValue;
        bool boolValue;
        layouts::godot_vector2_layout vector2Value;
        layouts::godot_rect2_layout  rect2Value;
        layouts::godot_vector3_layout vector3Value;
    } data;

    explicit ICallValue(KVariant::TypeCase type);
    explicit ICallValue(const KVariant& value);

    KVariant toKVariant();
    ~ICallValue();
};

class ICallArgs {
public:
    ICallArgs() = default;
    ~ICallArgs() = default;

    void addArg(const KVariant& arg);

    std::vector<void*> asRawData();
private:
    std::vector<ICallValue> iCallArgs;
};


#endif //GODOT_LOADER_ICALL_ARGS_H

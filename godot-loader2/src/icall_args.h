#ifndef GODOT_LOADER_ICALL_ARGS_H
#define GODOT_LOADER_ICALL_ARGS_H
#include "godot.h"
#include "native_tvalue.h"
#include <vector>

struct ICallValue {
    KVariant::TypeCase type;
    union {
        bool boolValue;
        godot_string stringValue;
        long intValue;
        double realValue;
        void* ptrValue;
    } data;

    ICallValue(KVariant::TypeCase type);
    ICallValue(NativeTValue& value);
    ~ICallValue();
};

class ICallArgs {
public:
    ICallArgs(std::vector<NativeTValue>& args);
    ~ICallArgs();

    std::vector<void*> asRawData();
private:
    std::vector<ICallValue> iCallArgs;
};


#endif //GODOT_LOADER_ICALL_ARGS_H

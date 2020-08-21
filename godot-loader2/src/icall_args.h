#ifndef GODOT_LOADER_ICALL_ARGS_H
#define GODOT_LOADER_ICALL_ARGS_H
#include "godot.h"
#include "native_tvalue.h"
#include <vector>

class ICallArg {
public:
    KVariant::TypeCase type;
    union {
        bool boolValue;
        godot_string stringValue;
        long intValue;
        double realValue;
        void* ptrValue;
    } data;

    ICallArg(NativeTValue& value);
    ~ICallArg();
};

class ICallArgs {
public:
    ICallArgs(std::vector<NativeTValue>& args);
    ~ICallArgs();

    std::vector<void*> asRawData();
private:
    std::vector<ICallArg> iCallArgs;
};


#endif //GODOT_LOADER_ICALL_ARGS_H
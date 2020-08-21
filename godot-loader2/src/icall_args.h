#ifndef GODOT_LOADER_ICALL_ARGS_H
#define GODOT_LOADER_ICALL_ARGS_H
#include "godot.h"
#include "wire.pb.h"
#include <vector>

struct ICallValue {
    KVariant::TypeCase type;
    union {
        bool boolValue;
        godot_string stringValue;
        long longValue;
        double realValue;
        void* ptrValue;
    } data;

    ICallValue(KVariant::TypeCase type);
    ICallValue(const KVariant& value);

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

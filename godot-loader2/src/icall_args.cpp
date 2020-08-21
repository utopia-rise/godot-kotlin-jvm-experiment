#include "icall_args.h"

static void(*TO_ICALL_ARG[27 /* KVariant::TypeCase count */])(ICallValue&, const KVariant&);
static bool TO_ICALL_ARG_INIT = false;
static void initToICallArg();

ICallValue::ICallValue(NativeTValue& value) {
    initToICallArg();
    type = value.data.type_case();
    auto converter = TO_ICALL_ARG[type - 1];
    converter(*this, value.data);
}

ICallValue::ICallValue(KVariant::TypeCase type) : type(type), data({}) {}

ICallValue::~ICallValue() {
    if (type == KVariant::TypeCase::kStringValue) {
        auto& godot = Godot::instance();
        godot.gd->godot_string_destroy(&data.stringValue);
    }
}

ICallArgs::ICallArgs(std::vector<NativeTValue>& args) {
    for (auto arg : args) {
        iCallArgs.emplace_back(ICallValue(arg));
    }
}

ICallArgs::~ICallArgs() = default;

std::vector<void*> ICallArgs::asRawData() {
    auto ret = std::vector<void*>();
    auto ptr = iCallArgs.data();
    for (auto i = 0; i <= iCallArgs.size(); i++) {
        ret.emplace_back(&(ptr[i].data));
    }
    return ret;
}

void nilToRawData(ICallValue& raw, const KVariant& data) {
    raw.data.ptrValue = nullptr;
}

void intToRawData(ICallValue& raw, const KVariant& data) {
    raw.data.intValue = data.long_value();
}

void realToRawData(ICallValue& raw, const KVariant& data) {
    raw.data.realValue = data.real_value();
}

void boolToRawData(ICallValue& raw, const KVariant& data) {
    raw.data.boolValue = data.bool_value();
}

void stringToRawData(ICallValue& raw, const KVariant& data) {
    auto& godot = Godot::instance();
    godot.gd->godot_string_new(&raw.data.stringValue);
    godot.gd->godot_string_parse_utf8(&raw.data.stringValue, data.string_value().c_str());
}

void initToICallArg() {
    if (TO_ICALL_ARG_INIT) {
        return;
    }

    // proto ordinals starts at 1
    TO_ICALL_ARG[KVariant::TypeCase::kNilValue - 1] = nilToRawData;
    TO_ICALL_ARG[KVariant::TypeCase::kLongValue - 1] = intToRawData;
    TO_ICALL_ARG[KVariant::TypeCase::kBoolValue - 1] = boolToRawData;
    TO_ICALL_ARG[KVariant::TypeCase::kRealValue - 1] = realToRawData;
    TO_ICALL_ARG[KVariant::TypeCase::kStringValue - 1] = stringToRawData;

    TO_ICALL_ARG_INIT = true;
}

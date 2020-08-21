#include "icall_args.h"

#define to_icall_from(type) \
    const int to_icall_from_##type##_index = KVariant::type - 1; \
    void to_icall_from_##type(ICallValue& dest, const KVariant& src)
#define to_icall_from_index(type) \
    [to_icall_from_##type##_index] = to_icall_from_##type

#define from_icall_to(type) \
    const int from_icall_to##type##_index = KVariant::type - 1; \
    void from_icall_to##type(KVariant& dest, const ICallValue& src)
#define from_icall_to_index(type) \
    [from_icall_to##type##_index] = from_icall_to##type

to_icall_from(kNilValue)  {
    dest.data.ptrValue = nullptr;
}

to_icall_from(kLongValue) {
    dest.data.longValue = src.long_value();
}

to_icall_from(kRealValue) {
    dest.data.realValue = src.real_value();
}

to_icall_from(kStringValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_string_new(&dest.data.stringValue);
    godot.gd->godot_string_parse_utf8(&dest.data.stringValue, src.string_value().c_str());
}

to_icall_from(kBoolValue) {
    dest.data.boolValue = src.bool_value();
}

static void(*TO_ICALL_FROM[27 /* KVariant::TypeCase count */])(ICallValue&, const KVariant&) = {
        to_icall_from_index(kNilValue),
        to_icall_from_index(kLongValue),
        to_icall_from_index(kRealValue),
        to_icall_from_index(kStringValue),
        to_icall_from_index(kBoolValue),
};

from_icall_to(kNilValue) {
    dest.set_nil_value(0);
}

from_icall_to(kLongValue) {
    dest.set_long_value(src.data.longValue);
}

from_icall_to(kRealValue) {
    dest.set_long_value(src.data.realValue);
}

from_icall_to(kStringValue) {
    auto& godot = Godot::instance();
    auto str = godot.fromGDString(&src.data.stringValue);
    dest.set_string_value(str);
    godot.gd->godot_string_destroy((godot_string*) &src.data.stringValue);
}

from_icall_to(kBoolValue) {
    dest.set_bool_value(src.data.boolValue);
}

static void(*FROM_ICALL_TO[27 /* KVariant::TypeCase count */])(KVariant&, const ICallValue&) = {
        from_icall_to_index(kNilValue),
        from_icall_to_index(kLongValue),
        from_icall_to_index(kRealValue),
        from_icall_to_index(kStringValue),
        from_icall_to_index(kBoolValue),
};

ICallValue::ICallValue(const KVariant& value) {
    type = value.type_case();
    auto converter = TO_ICALL_FROM[type - 1];
    converter(*this, value);
}

ICallValue::ICallValue(KVariant::TypeCase type) : type(type), data({}) {}

ICallValue::~ICallValue() {
    if (type == KVariant::TypeCase::kStringValue) {
        auto& godot = Godot::instance();
        godot.gd->godot_string_destroy(&data.stringValue);
    }
}

KVariant ICallValue::toKVariant() {
    auto variant = KVariant();
    auto converter = FROM_ICALL_TO[type - 1];
    converter(variant, *this);
    return variant;
}

void ICallArgs::addArg(const KVariant& arg) {
    iCallArgs.emplace_back(ICallValue(arg));
}

std::vector<void*> ICallArgs::asRawData() {
    auto ret = std::vector<void*>();
    auto ptr = iCallArgs.data();
    for (auto i = 0; i <= iCallArgs.size(); i++) {
        ret.emplace_back(&(ptr[i].data));
    }
    return ret;
}

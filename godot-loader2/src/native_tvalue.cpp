#include "native_tvalue.h"
#include "godot.h"

#define to_kvariant_from(type) \
    const int to_kvariant_from##type##_index = godot_variant_type::type; \
    void to_kvariant_from##type(KVariant& des, const godot_variant& src)
#define to_kvariant_from_index(type) \
    [to_kvariant_from##type##_index] = to_kvariant_from##type

to_kvariant_from(GODOT_VARIANT_TYPE_NIL) {
    des.set_nil_value(0);
}

to_kvariant_from(GODOT_VARIANT_TYPE_INT) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_int(&src);
    des.set_long_value(value);
}

to_kvariant_from(GODOT_VARIANT_TYPE_REAL) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_real(&src);
    des.set_real_value(value);
}

to_kvariant_from(GODOT_VARIANT_TYPE_STRING) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_string(&src);
    auto str = godot.fromGDString(&value);
    des.set_string_value(str.c_str());
}

to_kvariant_from(GODOT_VARIANT_TYPE_BOOL) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_bool(&src);
    des.set_bool_value(value);
}

// must match the value order of godot_variant_type
static void(*TO_KVARIANT_FROM[27 /* godot_variant_type count */])(KVariant&, const godot_variant&) = {
        to_kvariant_from_index(GODOT_VARIANT_TYPE_NIL),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_BOOL),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_INT),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_REAL),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_STRING),
};

#define from_kvariant_to(type) \
    const int from_kvariant_to##type##_index = KVariant::type - 1; \
    void from_kvariant_to##type(godot_variant& des, const KVariant& src)
#define from_kvariant_to_index(type) \
    [from_kvariant_to##type##_index] = from_kvariant_to##type

from_kvariant_to(kNilValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_nil(&des);
}

from_kvariant_to(kLongValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_int(&des, src.long_value());
}

from_kvariant_to(kRealValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_real(&des, src.real_value());
}

from_kvariant_to(kStringValue) {
    auto& godot = Godot::instance();
    godot_string str;
    godot.gd->godot_string_new(&str);
    godot.gd->godot_string_parse_utf8(&str, src.string_value().c_str());
    godot.gd->godot_variant_new_string(&des, &str);
    godot.gd->godot_string_destroy(&str);
}

from_kvariant_to(kBoolValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_bool(&des, src.bool_value());
}


// must match the value order of KVariant::TypeCase
static void(*TO_GVARIANT[27 /* KVariant::TypeCase count */])(godot_variant&, const KVariant&) = {
        from_kvariant_to_index(kNilValue),
        from_kvariant_to_index(kLongValue),
        from_kvariant_to_index(kRealValue),
        from_kvariant_to_index(kStringValue),
        from_kvariant_to_index(kBoolValue),
};

NativeTValue::NativeTValue(KVariant  data) : data(std::move(data)) {}

NativeTValue::NativeTValue(godot_variant variant) {
    auto& godot = Godot::instance();
    // TODO: use tristan's godot_variant_layout
    auto type = godot.gd->godot_variant_get_type(&variant);
    auto converter = TO_KVARIANT_FROM[type];
    converter(data, variant);
}

NativeTValue::~NativeTValue() {
}

godot_variant NativeTValue::toGVariant() {
    godot_variant variant;
    auto type = data.type_case();
    auto converter = TO_GVARIANT[type - 1];
    converter(variant, data);
    return variant;
}

#include "native_tvalue.h"

#include <utility>
#include "godot.h"

static void(*TO_KVARIANT[27 /* godot_variant_type count */])(KVariant&, const godot_variant&) ;
static bool TO_KVARIANT_INIT = false;
void initToKVariant();

static void(*TO_GVARIANT[27 /* KVariant::TypeCase count */])(godot_variant&, const KVariant&);
static bool TO_GVARIANT_INIT = false;
void initToGVariant();

NativeTValue::NativeTValue(KVariant  data) : data(std::move(data)) {}

NativeTValue::NativeTValue(godot_variant variant) {
    initToKVariant();
    auto& godot = Godot::instance();
    // TODO: use tristan's godot_variant_layout
    auto type = godot.gd->godot_variant_get_type(&variant);
    auto converter = TO_KVARIANT[type];
    converter(data, variant);
}

NativeTValue::~NativeTValue() {
}

godot_variant NativeTValue::toGVariant() {
    initToGVariant();
    godot_variant variant;
    auto type = data.type_case();
    auto converter = TO_GVARIANT[type - 1];
    converter(variant, data);
    return variant;
}

void nil64ToKVariant(KVariant& des, const godot_variant& variant) {
    des.set_nil_value(0);
}

void int64ToKVariant(KVariant& des, const godot_variant& variant) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_int(&variant);
    des.set_long_value(value);
}

void realToKVariant(KVariant& des, const godot_variant& variant) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_real(&variant);
    des.set_real_value(value);
}

void stringToKVariant(KVariant& des, const godot_variant& variant) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_string(&variant);
    auto str = godot.fromGDString(&value);
    des.set_string_value(str.c_str());
}


void boolToKVariant(KVariant& des, const godot_variant& variant) {
    auto& godot = Godot::instance();
    auto value = godot.gd->godot_variant_as_bool(&variant);
    des.set_bool_value(value);
}


void initToKVariant() {
    if (TO_KVARIANT_INIT) {
        return;
    }

    TO_KVARIANT[godot_variant_type::GODOT_VARIANT_TYPE_NIL] = nil64ToKVariant;
    TO_KVARIANT[godot_variant_type::GODOT_VARIANT_TYPE_INT] = int64ToKVariant;
    TO_KVARIANT[godot_variant_type::GODOT_VARIANT_TYPE_BOOL] = boolToKVariant;
    TO_KVARIANT[godot_variant_type::GODOT_VARIANT_TYPE_REAL] = realToKVariant;
    TO_KVARIANT[godot_variant_type::GODOT_VARIANT_TYPE_STRING] = stringToKVariant;

    TO_KVARIANT_INIT = true;
}

void nilToGVariant(godot_variant& variant, const KVariant& data) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_nil(&variant);
}

void intToGVariant(godot_variant& variant, const KVariant& data) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_int(&variant, data.long_value());
}

void realToGVariant(godot_variant& variant, const KVariant& data) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_real(&variant, data.real_value());
}

void boolToGVariant(godot_variant& variant, const KVariant& data) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_bool(&variant, data.bool_value());
}

void stringToGVariant(godot_variant& variant, const KVariant& data) {
    auto& godot = Godot::instance();
    godot_string str;
    godot.gd->godot_string_new(&str);
    godot.gd->godot_string_parse_utf8(&str, data.string_value().c_str());
    godot.gd->godot_variant_new_string(&variant, &str);
    godot.gd->godot_string_destroy(&str);
}

void initToGVariant() {
    if (TO_GVARIANT_INIT) {
        return;
    }

    // proto ordinals starts at 1
    TO_GVARIANT[KVariant::TypeCase::kNilValue - 1] = nilToGVariant;
    TO_GVARIANT[KVariant::TypeCase::kLongValue - 1] = intToGVariant;
    TO_GVARIANT[KVariant::TypeCase::kBoolValue - 1] = boolToGVariant;
    TO_GVARIANT[KVariant::TypeCase::kRealValue - 1] = realToGVariant;
    TO_GVARIANT[KVariant::TypeCase::kStringValue - 1] = stringToGVariant;

    TO_GVARIANT_INIT = true;
}
#include "native_tvalue.h"
#include "godot.h"
#include "layouts.h"

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

to_kvariant_from(GODOT_VARIANT_TYPE_VECTOR2) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto vec2 = Vector2::default_instance().New();
    vec2->set_x(layout->data._vect2.x);
    vec2->set_y(layout->data._vect2.y);
    des.set_allocated_vector2_value(vec2);
}

to_kvariant_from(GODOT_VARIANT_TYPE_RECT2) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto rect2 = Rect2::default_instance().New();
    rect2->mutable_position()->set_x(layout->data._rect2.position.x);
    rect2->mutable_position()->set_y(layout->data._rect2.position.y);
    rect2->mutable_size()->set_x(layout->data._rect2.size.x);
    rect2->mutable_size()->set_y(layout->data._rect2.size.y);
    des.set_allocated_rect2_value(rect2);
}

to_kvariant_from(GODOT_VARIANT_TYPE_VECTOR3) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto vec3 = Vector3::default_instance().New();
    vec3->set_x(layout->data._vect3.x);
    vec3->set_y(layout->data._vect3.y);
    vec3->set_z(layout->data._vect3.z);
    des.set_allocated_vector3_value(vec3);
}

// must match the value order of godot_variant_type
static void(*TO_KVARIANT_FROM[27 /* godot_variant_type count */])(KVariant&, const godot_variant&) = {
        to_kvariant_from_index(GODOT_VARIANT_TYPE_NIL),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_BOOL),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_INT),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_REAL),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_STRING),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_VECTOR2),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_RECT2),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_VECTOR3),
};

#define to_gvariant_from(type) \
    const int from_kvariant_to##type##_index = KVariant::type - 1; \
    void from_kvariant_to##type(godot_variant& des, const KVariant& src)
#define to_gvariant_from_index(type) \
    [from_kvariant_to##type##_index] = from_kvariant_to##type

to_gvariant_from(kNilValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_nil(&des);
}

to_gvariant_from(kLongValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_int(&des, src.long_value());
}

to_gvariant_from(kRealValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_real(&des, src.real_value());
}

to_gvariant_from(kStringValue) {
    auto& godot = Godot::instance();
    godot_string str;
    godot.gd->godot_string_new(&str);
    godot.gd->godot_string_parse_utf8(&str, src.string_value().c_str());
    godot.gd->godot_variant_new_string(&des, &str);
    godot.gd->godot_string_destroy(&str);
}

to_gvariant_from(kBoolValue) {
    auto& godot = Godot::instance();
    godot.gd->godot_variant_new_bool(&des, src.bool_value());
}

to_gvariant_from(kVector2Value) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_VECTOR2;
    layout->data._vect2.x = src.vector2_value().x();
    layout->data._vect2.y = src.vector2_value().y();
}

to_gvariant_from(kRect2Value) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_RECT2;
    layout->data._rect2.position.x = src.rect2_value().position().x();
    layout->data._rect2.position.y = src.rect2_value().position().y();
    layout->data._rect2.size.x = src.rect2_value().size().x();
    layout->data._rect2.size.y = src.rect2_value().size().y();
}

to_gvariant_from(kVector3Value) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_VECTOR3;
    layout->data._vect3.x = src.vector3_value().x();
    layout->data._vect3.y = src.vector3_value().y();
    layout->data._vect3.z = src.vector3_value().z();
}

// must match the value order of KVariant::TypeCase
static void(*TO_GVARIANT_FROM[27 /* KVariant::TypeCase count */])(godot_variant&, const KVariant&) = {
        to_gvariant_from_index(kNilValue),
        to_gvariant_from_index(kLongValue),
        to_gvariant_from_index(kRealValue),
        to_gvariant_from_index(kStringValue),
        to_gvariant_from_index(kBoolValue),
        to_gvariant_from_index(kVector2Value),
        to_gvariant_from_index(kRect2Value),
        to_gvariant_from_index(kVector3Value),
};

NativeTValue::NativeTValue(KVariant  data) : data(std::move(data)) {}

NativeTValue::NativeTValue(godot_variant variant) {
    auto& godot = Godot::instance();
    auto layout = (layouts::godot_variant_layout*) &variant;
    auto type = layout->type;
    auto converter = TO_KVARIANT_FROM[type];
    converter(data, variant);
}

NativeTValue::~NativeTValue() {
    data.clear_type();
}

godot_variant NativeTValue::toGVariant() {
    godot_variant variant;
    auto type = data.type_case();
    auto converter = TO_GVARIANT_FROM[type - 1];
    converter(variant, data);
    return variant;
}

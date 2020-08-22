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

inline Vector2* to_wire_vector2(const layouts::godot_vector2_layout& from) {
    auto vec2 = Vector2::default_instance().New();
    vec2->set_x(from.x);
    vec2->set_y(from.y);
    return vec2;
}

to_kvariant_from(GODOT_VARIANT_TYPE_VECTOR2) {
    auto layout = (layouts::godot_variant_layout*) &src;
    des.set_allocated_vector2_value(to_wire_vector2(layout->data._vect2));
}

to_kvariant_from(GODOT_VARIANT_TYPE_RECT2) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto rect2 = Rect2::default_instance().New();
    rect2->set_allocated_position(to_wire_vector2(layout->data._rect2.position));
    rect2->set_allocated_size(to_wire_vector2(layout->data._rect2.size));
    des.set_allocated_rect2_value(rect2);
}

inline Vector3* to_wire_vector3(const layouts::godot_vector3_layout& from) {
    auto vec3 = Vector3::default_instance().New();
    vec3->set_x(from.x);
    vec3->set_y(from.y);
    vec3->set_z(from.z);
    return vec3;
}

to_kvariant_from(GODOT_VARIANT_TYPE_VECTOR3) {
    auto layout = (layouts::godot_variant_layout*) &src;
    des.set_allocated_vector3_value(to_wire_vector3(layout->data._vect3));
}

to_kvariant_from(GODOT_VARIANT_TYPE_TRANSFORM2D) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto transform2d = Transform2D::default_instance().New();
    transform2d->set_allocated_x(to_wire_vector2(layout->data._transform2d->x));
    transform2d->set_allocated_y(to_wire_vector2(layout->data._transform2d->y));
    transform2d->set_allocated_origin(to_wire_vector2(layout->data._transform2d->origin));
    des.set_allocated_transform2d_value(transform2d);
}

to_kvariant_from(GODOT_VARIANT_TYPE_PLANE) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto plane = Plane::default_instance().New();
    plane->set_allocated_normal(to_wire_vector3(layout->data._plane.normal));
    plane->set_d(layout->data._plane.d);
    des.set_allocated_plane_value(plane);
}

to_kvariant_from(GODOT_VARIANT_TYPE_QUAT) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto quat = Quat::default_instance().New();
    quat->set_x(layout->data._quat.x);
    quat->set_y(layout->data._quat.y);
    quat->set_z(layout->data._quat.z);
    quat->set_w(layout->data._quat.w);
    des.set_allocated_quat_value(quat);
}

to_kvariant_from(GODOT_VARIANT_TYPE_AABB) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto aabb = AABB::default_instance().New();
    aabb->set_allocated_position(to_wire_vector3(layout->data._aabb->position));
    aabb->set_allocated_size(to_wire_vector3(layout->data._aabb->size));
    des.set_allocated_aabb_value(aabb);
}

inline Basis* to_wire_basis(const layouts::godot_basis_layout* data) {
    auto basis = Basis::default_instance().New();
    basis->set_allocated_x(to_wire_vector3(data->x));
    basis->set_allocated_y(to_wire_vector3(data->y));
    basis->set_allocated_z(to_wire_vector3(data->z));
    return basis;
}

to_kvariant_from(GODOT_VARIANT_TYPE_BASIS) {
    auto layout = (layouts::godot_variant_layout*) &src;
    des.set_allocated_basis_value(to_wire_basis(layout->data._basis));
}

to_kvariant_from(GODOT_VARIANT_TYPE_TRANSFORM) {
    auto layout = (layouts::godot_variant_layout*) &src;
    auto transform = Transform::default_instance().New();
    transform->set_allocated_basis(to_wire_basis(&layout->data._transform->basis));
    transform->set_allocated_origin(to_wire_vector3(layout->data._transform->origin));
    des.set_allocated_transform_value(transform);
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
        to_kvariant_from_index(GODOT_VARIANT_TYPE_TRANSFORM2D),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_PLANE),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_QUAT),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_AABB),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_BASIS),
        to_kvariant_from_index(GODOT_VARIANT_TYPE_TRANSFORM),
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

inline layouts::godot_vector2_layout to_godot_vector2(const Vector2& data) {
    layouts::godot_vector2_layout ret;
    ret.x = data.x();
    ret.y = data.y();
    return ret;
}

to_gvariant_from(kVector2Value) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_VECTOR2;
    layout->data._vect2 = to_godot_vector2(src.vector2_value());
}

to_gvariant_from(kRect2Value) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_RECT2;
    layout->data._rect2.position = to_godot_vector2(src.rect2_value().position());
    layout->data._rect2.size = to_godot_vector2(src.rect2_value().size());
}

inline layouts::godot_vector3_layout to_godot_vector3(const Vector3& data) {
    layouts::godot_vector3_layout ret;
    ret.x = data.x();
    ret.y = data.y();
    ret.z = data.z();
    return ret;
}

to_gvariant_from(kVector3Value) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_VECTOR3;
    layout->data._vect3 = to_godot_vector3(src.vector3_value());
}

to_gvariant_from(kTransform2DValue) {
    auto& gd = Godot::instance();
    layouts::godot_transform2d_layout transform2d;
    transform2d.x = to_godot_vector2(src.transform2d_value().x());
    transform2d.y = to_godot_vector2(src.transform2d_value().y());
    transform2d.origin = to_godot_vector2(src.transform2d_value().origin());
    gd.gd->godot_variant_new_transform2d(&des, (godot_transform2d*) &transform2d);
}

to_gvariant_from(kPlaneValue) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_PLANE;
    layout->data._plane.normal = to_godot_vector3(src.plane_value().normal());
    layout->data._plane.d = src.plane_value().d();
}

to_gvariant_from(kQuatValue) {
    auto layout = (layouts::godot_variant_layout*) &des;
    layout->type = GODOT_VARIANT_TYPE_QUAT;
    layout->data._quat.x = src.quat_value().x();
    layout->data._quat.y = src.quat_value().y();
    layout->data._quat.z = src.quat_value().z();
    layout->data._quat.w = src.quat_value().w();
}

to_gvariant_from(kAabbValue) {
    auto& gd = Godot::instance();
    layouts::godot_aabb_layout aabb;
    aabb.position = to_godot_vector3(src.aabb_value().position());
    aabb.size = to_godot_vector3(src.aabb_value().size());
    gd.gd->godot_variant_new_aabb(&des, (godot_aabb*) &aabb);
}

inline layouts::godot_basis_layout to_godot_basis(const Basis& data) {
    layouts::godot_basis_layout ret;
    ret.x = to_godot_vector3(data.x());
    ret.y = to_godot_vector3(data.y());
    ret.z = to_godot_vector3(data.z());
    return ret;
}

to_gvariant_from(kBasisValue) {
    auto& gd = Godot::instance();
    auto basis = to_godot_basis(src.basis_value());
    gd.gd->godot_variant_new_basis(&des, (godot_basis*) &basis);
}

to_gvariant_from(kTransformValue) {
    auto& gd = Godot::instance();
    layouts::godot_transform_layout transform;
    transform.basis = to_godot_basis(src.transform_value().basis());
    transform.origin = to_godot_vector3(src.transform_value().origin());
    gd.gd->godot_variant_new_transform(&des, (godot_transform*) &transform);
}

// must match the value order of KVariant::TypeCase
static void(*TO_GVARIANT_FROM[27 /* KVariant::TypeCase count */])(godot_variant&, const KVariant&) = {
        to_gvariant_from_index(kNilValue),
        to_gvariant_from_index(kBoolValue),
        to_gvariant_from_index(kLongValue),
        to_gvariant_from_index(kRealValue),
        to_gvariant_from_index(kStringValue),
        to_gvariant_from_index(kVector2Value),
        to_gvariant_from_index(kRect2Value),
        to_gvariant_from_index(kVector3Value),
        to_gvariant_from_index(kTransform2DValue),
        to_gvariant_from_index(kPlaneValue),
        to_gvariant_from_index(kQuatValue),
        to_gvariant_from_index(kAabbValue),
        to_gvariant_from_index(kBasisValue),
        to_gvariant_from_index(kTransformValue),
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

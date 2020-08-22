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

inline layouts::godot_vector2_layout to_raw_vector2(const Vector2& data) {
    layouts::godot_vector2_layout ret;
    ret.x = data.x();
    ret.y = data.y();
    return ret;
}

to_icall_from(kVector2Value) {
    dest.data.vector2Value = to_raw_vector2(src.vector2_value());
}

to_icall_from(kRect2Value) {
    dest.data.rect2Value.position = to_raw_vector2(src.rect2_value().position());
    dest.data.rect2Value.size = to_raw_vector2(src.rect2_value().size());
}


inline layouts::godot_vector3_layout to_raw_vector3(const Vector3& data) {
    layouts::godot_vector3_layout ret;
    ret.x = data.x();
    ret.y = data.y();
    ret.z = data.z();
    return ret;
}

to_icall_from(kVector3Value) {
    dest.data.vector3Value = to_raw_vector3(src.vector3_value());
}

to_icall_from(kTransform2DValue) {
    dest.data.transform2DValue.x = to_raw_vector2(src.transform2d_value().x());
    dest.data.transform2DValue.y = to_raw_vector2(src.transform2d_value().y());
    dest.data.transform2DValue.origin = to_raw_vector2(src.transform2d_value().origin());
}

to_icall_from(kPlaneValue) {
    dest.data.planeValue.normal = to_raw_vector3(src.plane_value().normal());
    dest.data.planeValue.d = src.plane_value().d();
}

to_icall_from(kQuatValue) {
    dest.data.quatValue.x = src.quat_value().x();
    dest.data.quatValue.y = src.quat_value().y();
    dest.data.quatValue.z = src.quat_value().z();
    dest.data.quatValue.w = src.quat_value().w();
}

to_icall_from(kAabbValue) {
    dest.data.aabbValue.position = to_raw_vector3(src.aabb_value().position());
    dest.data.aabbValue.size = to_raw_vector3(src.aabb_value().size());
}

inline layouts::godot_basis_layout to_raw_basis(const Basis& data) {
    layouts::godot_basis_layout ret;
    ret.x = to_raw_vector3(data.x());
    ret.y = to_raw_vector3(data.y());
    ret.z = to_raw_vector3(data.z());
    return ret;
}

to_icall_from(kBasisValue) {
    dest.data.basisLayout = to_raw_basis(src.basis_value());
}

to_icall_from(kTransformValue) {
    dest.data.transformValue.basis = to_raw_basis(src.transform_value().basis());
    dest.data.transformValue.origin = to_raw_vector3(src.transform_value().origin());
}

// must match the value order of KVariant::TypeCase
static void(*TO_ICALL_FROM[27 /* KVariant::TypeCase count */])(ICallValue&, const KVariant&) = {
        to_icall_from_index(kNilValue),
        to_icall_from_index(kBoolValue),
        to_icall_from_index(kLongValue),
        to_icall_from_index(kRealValue),
        to_icall_from_index(kStringValue),
        to_icall_from_index(kVector2Value),
        to_icall_from_index(kRect2Value),
        to_icall_from_index(kVector3Value),
        to_icall_from_index(kTransform2DValue),
        to_icall_from_index(kPlaneValue),
        to_icall_from_index(kQuatValue),
        to_icall_from_index(kAabbValue),
        to_icall_from_index(kBasisValue),
        to_icall_from_index(kTransformValue),
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

inline Vector2* to_wire_vector2(const layouts::godot_vector2_layout& from) {
    auto ret = Vector2::default_instance().New();
    ret->set_x(from.x);
    ret->set_y(from.y);
    return ret;
}

from_icall_to(kVector2Value) {
    dest.set_allocated_vector2_value(to_wire_vector2(src.data.vector2Value));
}

from_icall_to(kRect2Value) {
    auto rect2 = Rect2::default_instance().New();
    rect2->set_allocated_position(to_wire_vector2(src.data.rect2Value.position));
    rect2->set_allocated_size(to_wire_vector2(src.data.rect2Value.size));
    dest.set_allocated_rect2_value(rect2);
}

inline Vector3* to_wire_vector3(const layouts::godot_vector3_layout& from) {
    auto ret = Vector3::default_instance().New();
    ret->set_x(from.x);
    ret->set_y(from.y);
    ret->set_z(from.z);
    return ret;
}

from_icall_to(kVector3Value) {
    dest.set_allocated_vector3_value(to_wire_vector3(src.data.vector3Value));
}

from_icall_to(kTransform2DValue) {
    auto transform2D = Transform2D::default_instance().New();
    transform2D->set_allocated_x(to_wire_vector2(src.data.transform2DValue.x));
    transform2D->set_allocated_y(to_wire_vector2(src.data.transform2DValue.y));
    transform2D->set_allocated_origin(to_wire_vector2(src.data.transform2DValue.origin));
    dest.set_allocated_transform2d_value(transform2D);
}

from_icall_to(kPlaneValue) {
    auto plane = Plane::default_instance().New();
    plane->set_allocated_normal(to_wire_vector3(src.data.planeValue.normal));
    plane->set_d(src.data.planeValue.d);
    dest.set_allocated_plane_value(plane);
}

from_icall_to(kQuatValue) {
    auto quat = Quat::default_instance().New();
    quat->set_x(src.data.quatValue.x);
    quat->set_y(src.data.quatValue.y);
    quat->set_z(src.data.quatValue.z);
    quat->set_w(src.data.quatValue.w);
    dest.set_allocated_quat_value(quat);
}

from_icall_to(kAabbValue) {
    auto aabb = AABB::default_instance().New();
    aabb->set_allocated_position(to_wire_vector3(src.data.aabbValue.position));
    aabb->set_allocated_size(to_wire_vector3(src.data.aabbValue.size));
    dest.set_allocated_aabb_value(aabb);
}

inline Basis* to_wire_basis(const layouts::godot_basis_layout& data) {
    auto ret = Basis::default_instance().New();
    ret->set_allocated_x(to_wire_vector3(data.x));
    ret->set_allocated_y(to_wire_vector3(data.y));
    ret->set_allocated_z(to_wire_vector3(data.z));
    return ret;
}

from_icall_to(kBasisValue) {
    dest.set_allocated_basis_value(to_wire_basis(src.data.basisLayout));
}

from_icall_to(kTransformValue) {
    auto transform = Transform::default_instance().New();
    transform->set_allocated_basis(to_wire_basis(src.data.transformValue.basis));
    transform->set_allocated_origin(to_wire_vector3(src.data.transformValue.origin));
    dest.set_allocated_transform_value(transform);
}

// must match the value order of KVariant::TypeCase
static void(*FROM_ICALL_TO[27 /* KVariant::TypeCase count */])(KVariant&, const ICallValue&) = {
        from_icall_to_index(kNilValue),
        from_icall_to_index(kBoolValue),
        from_icall_to_index(kLongValue),
        from_icall_to_index(kRealValue),
        from_icall_to_index(kStringValue),
        from_icall_to_index(kVector2Value),
        from_icall_to_index(kRect2Value),
        from_icall_to_index(kVector3Value),
        from_icall_to_index(kTransform2DValue),
        from_icall_to_index(kPlaneValue),
        from_icall_to_index(kQuatValue),
        from_icall_to_index(kAabbValue),
        from_icall_to_index(kBasisValue),
        from_icall_to_index(kTransformValue),
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

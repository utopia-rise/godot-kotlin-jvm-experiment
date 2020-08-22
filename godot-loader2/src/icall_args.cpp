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

to_icall_from(kVector2Value) {
    dest.data.vector2Value.x = src.vector2_value().x();
    dest.data.vector2Value.y = src.vector2_value().y();
}

to_icall_from(kRect2Value) {
    dest.data.rect2Value.position.x = src.rect2_value().position().x();
    dest.data.rect2Value.position.y = src.rect2_value().position().y();
    dest.data.rect2Value.size.x = src.rect2_value().size().x();
    dest.data.rect2Value.size.y = src.rect2_value().size().y();
}

to_icall_from(kVector3Value) {
    dest.data.vector3Value.x = src.vector3_value().x();
    dest.data.vector3Value.y = src.vector3_value().y();
    dest.data.vector3Value.z = src.vector3_value().z();
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

from_icall_to(kVector2Value) {
    auto vec2 = Vector2::default_instance().New();
    vec2->set_x(src.data.vector2Value.x);
    vec2->set_y(src.data.vector2Value.y);
    dest.set_allocated_vector2_value(vec2);
}

from_icall_to(kRect2Value) {
    auto rect2 = Rect2::default_instance().New();
    rect2->mutable_position()->set_x(src.data.rect2Value.position.x);
    rect2->mutable_position()->set_y(src.data.rect2Value.position.y);
    rect2->mutable_size()->set_x(src.data.rect2Value.size.x);
    rect2->mutable_size()->set_y(src.data.rect2Value.size.y);
    dest.set_allocated_rect2_value(rect2);
}

from_icall_to(kVector3Value) {
    auto vec3 = Vector3::default_instance().New();
    vec3->set_x(src.data.vector3Value.x);
    vec3->set_y(src.data.vector3Value.y);
    vec3->set_z(src.data.vector3Value.z);
    dest.set_allocated_vector3_value(vec3);
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

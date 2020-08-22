extends "res://addons/gut/test.gd"

var CMP_EPSILON = 0.0001
var Invocation = preload("res://src/gdns/kotlin/godot/tests/Invocation.gdns")
var instance

func before_each():
    instance = Invocation.new()

func after_each():
    instance.free()
    instance = null

func test_long_value():
    var expected = 1972
    assert_eq(expected, instance.long_value(expected))

func test_int_value():
    var expected = 230
    assert_eq(expected, instance.int_value(expected))

func test_float_value():
    var expected = 0.02
    assert_almost_eq(expected, instance.float_value(expected), CMP_EPSILON)

func test_double_value():
    var expected = 0.004
    assert_almost_eq(expected, instance.double_value(expected), CMP_EPSILON)

func test_string_value():
    var expected = "Hello from Godot!"
    assert_eq(expected, instance.string_value(expected))

func test_bool_value():
    var expected = false
    assert_eq(expected, instance.bool_value(expected))

func test_vector2():
    var expected = Vector2(32, 0.5)
    assert_eq(expected, instance.vector2_value(expected))

func test_rect2():
    var expected = Rect2(Vector2(0.5, 1.0), Vector2(3.0, 2.0))
    assert_eq(expected, instance.rect2_value(expected))

func test_vector3():
    var expected = Vector3(32, 0.5, 0.05)
    assert_eq(expected, instance.vector3_value(expected))
extends Spatial

func _ready():
	assert_eq($simple.long_method(10023), 10023)
	assert_eq($simple.bool_method(false), false)
	assert_eq($simple.string_method("Hello from Godot!"), "Hello from Godot!")
	assert_eq($simple.vector2_method(Vector2(25, 3.0)), Vector2(25, 3.0))
	
	
func assert_eq(expected, actual):
	if expected != actual:
		print("Expected " + str(expected) + " but got a " + str(actual))
		assert(false)

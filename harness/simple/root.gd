extends Spatial

func _ready():
	print("root")
	$simple.long_method(5125)
	$simple.bool_method(false)
	$simple.string_method("Hello from root!")

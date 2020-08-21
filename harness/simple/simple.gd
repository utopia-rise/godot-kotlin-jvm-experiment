extends Spatial

func _process(delta: float):
	var a = deg2rad(30) * delta
	print(a)

	rotate_y(deg2rad(30) * delta)

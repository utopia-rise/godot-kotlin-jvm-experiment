package simple

import godot.core.Vector2
import godot.internal.BindingContext
import godot.internal.KObject
import godot.wire.TValue

class Simple : KObject() {
    init {
        println("Simple object created")
    }

    override fun _onInit() {
        println("Simple::_onInit")
    }

    override fun _onDestroy() {
        println("Simple::_onDestroy")
    }

    fun _ready() {
        println("Simple is ready!")
    }

    fun _process(delta: Float) {
        val transferContext = BindingContext.transferContext
        val value = Math.toRadians(30.0) * delta
        transferContext.writeArguments(TValue(value))
        transferContext.callMethod(hackPtr, "Spatial", "rotate_y", TValue.Type.NIL)
        transferContext.readReturnValue()
    }

    fun longMethod(a: Long) = a
    fun boolMethod(a: Boolean) = a
    fun stringMethod(a: String) = a
    fun vector2Method(a: Vector2) = a
}
package simple

import godot.internal.KObject

class Simple : KObject() {
    override fun _onInit() {
        println("Hello Godot from Simple!")
    }

    fun _ready() {
        println("Simple is ready!")
    }
}
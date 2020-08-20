package simple

import godot.internal.KObject

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

    fun _ready(): Int {
        println("Simple is ready!")
        return 100
    }

    fun _process(delta: Double) {
         //println("delta: $delta from godot!")
    }

    fun longMethod(a: Long) {
        println("Got long: $a")
    }

    fun boolMethod(a: Boolean) {
        println("Got bool: $a")
    }

    fun stringMethod(a: String) {
        println("Got string: $a")
    }
}
package com.example.temp.delegateexercise

import kotlin.reflect.KProperty

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }

    /*operator fun plus(b:Delegate):Delegate{
        return Delegate(a+b.a)
    }*/
}

class Example {
    var p: String by Delegate()

    /*val f = Delegate(2)
    val g = Delegate(2)
    val h = f + g*/
}

fun main(){
    val e = Example()
    println(e.p)

    e.p = "NEW"
}
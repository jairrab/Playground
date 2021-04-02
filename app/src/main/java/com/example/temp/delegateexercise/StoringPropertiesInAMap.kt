package com.example.temp.delegateexercise

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UserMap(val map: Map<String, Any?>) {
    val name: String by map
    val age: Int     by map
}

fun main(){
    val user = UserMap(mapOf(
        "name" to "John Doe",
        "age"  to 23
    ))

    println(user.name) // Prints "John Doe"
    println(user.age)  // Prints 25

    val readOnly: Int by resourceDelegate()  // ReadWriteProperty as val
    var readWrite: Int by resourceDelegate()


}

fun resourceDelegate(): ReadWriteProperty<Any?, Int> =
    object : ReadWriteProperty<Any?, Int> {
        var curValue = 0
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = curValue
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            curValue = value
        }
    }
package com.example.temp.exercises

sealed interface Animal
interface Cat : Animal
sealed interface Dog : Animal
interface SmallDog : Dog
sealed interface BigDog : Dog
interface BigBrownDog : BigDog
interface BigBlackDog : BigDog

class SealedInterfaceTest {
    fun test(animal: Animal) {
        when (animal) {
            is Cat -> TODO()
            is Dog -> when (animal) {
                is SmallDog -> TODO()
                is BigDog -> when (animal) {
                    is BigBlackDog -> TODO()
                    is BigBrownDog -> TODO()
                }
            }
        }
    }
}
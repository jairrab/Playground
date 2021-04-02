package com.example.temp.exercises

fun reconcileHelper(a : Array<Int>, b: Array<Int>) : String {
    val array1 = a.filterNot { b.contains(it) }.sorted().joinToString()
    val array2 = b.filterNot { a.contains(it) }.sorted().joinToString()
    val set1 = "Numbers in array 1 that aren't in array 2:\n$array1"
    val set2 = "Numbers in array 2 that aren't in array 1:\n$array2"
    return "$set1\n\n$set2"
}

fun main() {
    val result = reconcileHelper(
        arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,65,54,4,25,54,6,5,4,53,7645,756,75,68,768,4,3,5,34,64,6),
        arrayOf(1, 2,3,4,5,6,7,8,9,0,12,13,45,56,67,87,24,43,434,65,76,7,343,43,54,54,656,57,54,76,7,6,76)
    )

    // Should print:
    // Numbers in array 1 that aren't in array 2:
    // 5
    //
    // Numbers in array 2 that aren't in array 1:
    // 6 10
    println(result)
}
package com.github.deminder

import kotlin.math.absoluteValue

fun solvePart1(leftNumbers: List<Int>, rightNumbers: List<Int>): Int {
    return leftNumbers.sorted()
        .zip(rightNumbers.sorted())
        .sumOf { (it.second - it.first).absoluteValue };
}

fun solvePart2(leftNumbers: List<Int>, rightNumbers: List<Int>): Int {
    val countOfNumberOccurrence = rightNumbers.groupingBy { it }.eachCount()
    return leftNumbers
        .sumOf { it * countOfNumberOccurrence.getOrDefault(it, 0) }

}

fun solveDay01(inputLines: Sequence<String>, part2: Boolean): String {
    val regex = """(\d+)\s+(\d+)""".toRegex()

    val (leftNumbers, rightNumbers) = inputLines
        .map { regex.find(it)!!.destructured }
        .map { Pair(it.component1().toInt(), it.component2().toInt()) }
        .unzip();
    val result = if (part2) solvePart2(leftNumbers, rightNumbers) else solvePart1(leftNumbers, rightNumbers)
    return result.toString()
}


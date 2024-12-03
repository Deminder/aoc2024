package com.github.deminder

val enabledParts: (String) -> Sequence<String> = { line ->
    line
        .splitToSequence("do()")
        .map { it.substringBefore("don't()") }
}

fun solveDay03(inputLines: Sequence<String>, part2: Boolean): String {
    val pattern = "mul\\((\\d+),(\\d+)\\)".toRegex()
    return (if (part2) enabledParts(inputLines.joinToString()) else inputLines)
        .flatMap { pattern.findAll(it) }
        .map { it.destructured }
        .map { (a, b) -> a.toInt() to b.toInt() }
        .sumOf { (a, b) -> a * b }
        .toString()
}


package com.github.deminder

object Day03 : Day {

    val enabledParts: (String) -> Sequence<String> = { line ->
        line
            .splitToSequence("do()")
            .map { it.substringBefore("don't()") }
    }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val pattern = "mul\\((\\d+),(\\d+)\\)".toRegex()
        return (if (part2) enabledParts(inputLines.joinToString()) else inputLines)
            .flatMap { pattern.findAll(it) }
            .map { it.destructured }
            .map { (a, b) -> a.toInt() to b.toInt() }
            .sumOf { (a, b) -> a * b }
            .toString()
    }
}


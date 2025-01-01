package com.github.deminder

import com.github.deminder.shared.split

object Day25 : Day {

    private fun doesKeyFitIntoLock(key: List<Int>, lock: List<Int>) =
        key.zip(lock).all { (k, l) -> k + l <= 5 }

    data class Schematics(
        val keys: List<List<Int>>,
        val locks: List<List<Int>>
    ) {
        fun countFittingPairs() = keys
            .sumOf { key -> locks.count { doesKeyFitIntoLock(key, it) } }
    }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .countFittingPairs()
            .toString()

    private fun List<String>.toHeights() = drop(1)
        .fold(this.first().map { 0 }) { heights, line ->
            heights.mapIndexed { column, height -> if (line[column] == '#') height + 1 else height }
        }

    private fun parse(inputLines: Sequence<String>) = inputLines.split { it.isBlank() }
        .partition { it.first().startsWith(".") }
        .let { (locks, keys) ->
            Schematics(
                locks.map { it.reversed().toHeights() },
                keys.map { it.toHeights() }
            )
        }


}
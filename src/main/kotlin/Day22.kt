package com.github.deminder

import com.github.deminder.shared.mapAsync
import kotlin.math.absoluteValue

object Day22 : Day {


    data class SecretNumber(
        val value: Long
    ) {
        private fun shiftAndMixAndPrune(shift: Int) =
            copy(
                value = (value xor (
                        if (shift >= 0)
                            value shl shift
                        else
                            value shr shift.absoluteValue
                        )) % 16777216
            )

        fun price() = value % 10

        fun next() = shiftAndMixAndPrune(6)
            .shiftAndMixAndPrune(-5)
            .shiftAndMixAndPrune(11)


        override fun toString() = value.toString()
    }

    fun evolve2000th(secretNumber: Long) =
        (1..2000)
            .asSequence()
            .runningFold(SecretNumber(secretNumber)) { number, _ ->
                number.next()
            }

    private fun firstPriceDiffWindows(secretNumber: Long) =
        evolve2000th(secretNumber)
            .map { it.price() }
            .windowed(2)
            .map { (a, b) -> b - a to b }
            .windowed(4)
            .groupingBy { it.map { (diff, _) -> diff } }
            .reduce { _, first, _ -> first }
            .mapValues { (_, window) -> window.last().second }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .let { numbers ->
                if (part2)
                    numbers
                        .toList()
                        .mapAsync { firstPriceDiffWindows(it) }
                        .flatMap { it.entries }
                        .groupBy { (window, _) -> window }
                        .map { (_, group) -> group.sumOf { (_, bananas) -> bananas } }
                        .max()
                else
                    numbers.sumOf { evolve2000th(it).last().value }

            }

            .toString()


    private fun parse(inputLines: Sequence<String>) = inputLines.map { it.toLong() }


}
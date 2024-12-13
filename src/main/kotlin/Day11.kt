package com.github.deminder

import java.math.BigDecimal

typealias Stones = List<Pair<BigDecimal, Long>>

object Day11 : Day {

    private fun BigDecimal.blink(): List<BigDecimal> = if (this == BigDecimal.ZERO)
        listOf(BigDecimal.ONE)
    else
        this.toPlainString().let { decimalString ->
            if (decimalString.length % 2 == 0)
                decimalString.chunked(decimalString.length / 2) { it.toString().toBigDecimal() }
            else
                listOf(this * BigDecimal.valueOf(2024))
        }

    private fun Stones.blink(times: Int) = generateSequence(this) { stones ->
        stones.flatMap { (stone, count) -> stone.blink().map { it to count } }
            .groupBy { (stone, _) -> stone }
            .map { (stone, stoneGroup) -> stone to stoneGroup.sumOf { (_, count) -> count } }
    }.take(times + 1).last()


    fun blink(inputLines: Sequence<String>, times: Int): Long {
        val stones = parse(inputLines)

        return stones.blink(times)
            .sumOf { (_, count) -> count }
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        blink(inputLines, if (part2) 75 else 25).toString()

    private fun parse(inputLines: Sequence<String>): Stones =
        inputLines.first().split(" ").map { it.toBigDecimal() to 1L }.toList()


}
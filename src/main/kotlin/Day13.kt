package com.github.deminder

import com.github.deminder.shared.*
import kotlin.math.roundToLong

private typealias Vec2f = Pair<Double, Double>
private typealias Vec2l = Pair<Long, Long>

object Day13 : Day {


    private fun Vec2l.toDoubles() = Vec2f(first.toDouble(), second.toDouble())
    operator fun Long.times(vec: Vec2l) = Vec2l(this * vec.first, this * vec.second)
    operator fun Vec2l.plus(vec: Vec2l) = Vec2l(this.first + vec.first, this.second + vec.second)

    data class Game(
        val buttonA: Vec2l,
        val buttonB: Vec2l,
        val prize: Vec2l
    ) {

        // prize + s * buttonA = t * buttonB
        private fun fullButtonAPresses(): Double? {
            val (a1, a2) = buttonA.toDoubles()
            val (b1, b2) = buttonB.toDoubles()
            val (x1, x2) = prize.toDoubles()
            val denominator = (a2 / b2) - (a1 / b1)
            return if (denominator == 0.0) {
                null
            } else {
                val s = ((x2 / b2) - (x1 / b1)) / denominator
                if (s > 0) s else null
            }
        }

        private fun fullButtonBPresses(fullButtonAPresses: Double): Double {
            val (a1, a2) = buttonA.toDoubles()
            val (b1, b2) = buttonB.toDoubles()
            val (x1, x2) = prize.toDoubles()
            return ((x1 / b1) + (x2 / b2) - fullButtonAPresses * ((a1 / b1) + (a2 / b2))) / 2
        }

        private fun checkWin(presses: Vec2l) = (presses.first * buttonA) + (presses.second * buttonB) == prize


        fun lowestCost() = fullButtonAPresses()
            ?.let { it to fullButtonBPresses(it) }
            ?.let { it.first.roundToLong() to it.second.roundToLong() }
            ?.let { if (checkWin(it)) it else null }
            ?.let { if (VERBOSE) println(it); it.first * 3 + it.second }

    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .map { if (part2) it.copy(prize = it.prize + Vec2l(10000000000000L, 10000000000000L)) else it }
            .sumOf { it.lowestCost() ?: 0 }
            .toString()

    private fun parseNumberPair(line: String) = "\\d+".toRegex().findAll(line)
        .map { it.value }
        .map { it.toLong() }
        .toList()
        .let {
            Vec2l(it[0], it[1])
        }

    private fun parse(inputLines: Sequence<String>): Sequence<Game> =
        inputLines
            .windowed(3, 4)
            .map { (a, b, p) ->
                Game(
                    buttonA = parseNumberPair(a),
                    buttonB = parseNumberPair(b),
                    prize = parseNumberPair(p)
                )
            }


}
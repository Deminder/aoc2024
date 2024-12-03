package com.github.deminder

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import java.io.File

fun interface Day {
    fun solve(inputLines: Sequence<String>, part2: Boolean): String
}

val DAYS = listOf(
    Day(::solveDay01),
    Day(::solveDay02),
    Day(::solveDay03),
)

fun main(args: Array<String>) {
    val parser = ArgParser("aoc2024")

    val dayNumber by parser.argument(
        ArgType.Choice((1..DAYS.size).toList(), { it }),
        fullName = "day",
        description = "Day of puzzle"
    )

    val partNumber by parser.argument(
        ArgType.Choice(listOf(1, 2), { it }),
        fullName = "part",
        description = "Part of puzzle to run"
    )

    val inputFileName by parser.option(
        ArgType.String,
        shortName = "i",
        fullName = "input-file",
        description = "Puzzle input file, otherwise reads from stdin"
    )
    parser.parse(args)
    println("[Day $dayNumber] [Part $partNumber] ${inputFileName?.let { "with input file $it." } ?: "with input from stdin."}")

    val day = DAYS[dayNumber.toString().toInt() - 1]
    val inputLines: Sequence<String> = inputFileName?.let {
        File(it).bufferedReader().lineSequence()
    } ?: generateSequence(::readLine)
    val part2 = partNumber == "2"

    val output = day.solve(inputLines, part2)
    println("Output: $output")
}
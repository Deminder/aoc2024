package com.github.deminder

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import java.io.File

fun interface Day {
    fun solve(inputLines: Sequence<String>, part2: Boolean): String
}


val DAYS = listOf(
    Day01,
    Day02,
    Day03,
    Day04,
    Day05,
    Day06,
    Day07,
    Day08,
    Day09,
    Day10,
    Day11,
    Day12,
    Day13,
    Day14,
    Day15,
    Day16,
    Day17,
    Day18,
    Day19,
    Day20,
    Day21,
    Day22,
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

    val dayIndex = dayNumber.toString().toInt() - 1
    if (dayIndex !in DAYS.indices) {
        error("Invalid day number $dayNumber")
    }

    val day = DAYS[dayIndex]
    val inputLines: Sequence<String> = inputFileName?.let {
        File(it).bufferedReader().lineSequence()
    } ?: generateSequence(::readLine)
    val part2 = partNumber == "2"

    val output = day.solve(inputLines, part2)
    println("Output: $output")
}
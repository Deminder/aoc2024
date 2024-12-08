package com.github.deminder

private typealias Rules = Map<Int, Set<Int>>

object Day05 : Day {

    private fun doesLineBreakAnyRule(numberLine: List<Int>, firstBeforeSecondRules: Rules): Boolean {
        return numberLine
            .mapIndexed { i, b ->
                numberLine
                    .slice(0..<i)
                    .any { a -> firstBeforeSecondRules[b]?.contains(a) ?: false }
            }.any { it }
    }

    private fun sortLineByRules(numberLine: List<Int>, firstBeforeSecondRules: Rules): List<Int> {
        return numberLine.sortedWith { a, b ->
            when {
                firstBeforeSecondRules[a]?.contains(b) ?: false -> 1
                firstBeforeSecondRules[b]?.contains(a) ?: false -> -1
                else -> 0
            }
        }
    }

    private fun parse(inputLines: Sequence<String>): Pair<List<List<Int>>, Rules> {
        val (rulePart, challengePart) = inputLines.joinToString("\n").split("\n\n")

        val firstBeforeSecondRules: Rules = rulePart.lines()
            .map { line -> line.split("|").map { it.toInt() } }
            .groupingBy { (a, _) -> a }
            .fold(emptySet()) { bs, (_, b) -> bs.plus(b) }

        val numberLines: List<List<Int>> = challengePart.lines()
            .map { line -> line.split(",").map(String::toInt) }

        return numberLines to firstBeforeSecondRules
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val (numberLines, firstBeforeSecondRules) = parse(inputLines)

        return if (part2) {
            numberLines
                .filter { doesLineBreakAnyRule(it, firstBeforeSecondRules) }
                .map { sortLineByRules(it, firstBeforeSecondRules) }
        } else {
            numberLines
                .filterNot { doesLineBreakAnyRule(it, firstBeforeSecondRules) }
        }
            .sumOf { it[it.size / 2] }
            .toString()
    }

}
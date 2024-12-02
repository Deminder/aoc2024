package com.github.deminder

private fun findFirstBadIndex(report: List<Int>): Int {
    return report
        .zipWithNext { a, b -> b - a }
        .indexOfFirst { it !in 1..3 }
}

private fun reportWithoutBadIndex(report: List<Int>, badIndex: Int): List<Int> {
    return report.filterIndexed { index, _ -> index != badIndex }
}

private fun isSafeIncreasing(report: List<Int>, dampener: Boolean): Boolean {
    return findFirstBadIndex(report).let {
        it == -1 || (dampener && (0..1).any { offset ->
            isSafeIncreasing(
                reportWithoutBadIndex(report, it + offset),
                false
            )
        })
    }
}

private fun isSafeIncreasingOrDecreasing(report: List<Int>, damper: Boolean): Boolean {
    return isSafeIncreasing(report, damper) || isSafeIncreasing(report.map { -it }, damper)
}

private fun solvePart1(reports: List<List<Int>>): Int {
    return reports.count { isSafeIncreasingOrDecreasing(it, false) }
}

private fun solvePart2(reports: List<List<Int>>): Int {
    return reports.count { isSafeIncreasingOrDecreasing(it, true) }
}

fun solveDay02(inputLines: Sequence<String>, part2: Boolean): String {

    val reports = inputLines
        .map { it.split("\\s+".toRegex()).map(String::toInt).toList() }
        .toList()
    val result = if (part2) solvePart2(reports) else solvePart1(reports)
    return result.toString()
}


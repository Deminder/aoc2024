package com.github.deminder

import com.github.deminder.shared.*


private data class Antenna(
    val pos: Vec2,
    val frequency: Char
)

private fun List<Vec2>.antiNodesForOneFrequency(harmonics: Boolean, bounds: Vec2): List<Vec2> = flatMap { primary ->
    this.filter { it != primary }
        .flatMap { secondary ->
            (secondary - primary).let { diff ->
                if (harmonics) {
                    generateSequence(primary) { it - diff }
                } else {
                    sequenceOf(primary - diff)
                }
            }.takeWhile { node -> node inBounds bounds }
        }
}

private fun List<Antenna>.antiNodesForAllFrequencies(harmonics: Boolean, bounds: Vec2): List<Vec2> =
    groupBy { antenna -> antenna.frequency }
        .flatMap { (_, antennaOfSameFrequency) ->
            antennaOfSameFrequency
                .map { a -> a.pos }
                .antiNodesForOneFrequency(harmonics, bounds)
        }


fun solveDay08(inputLines: Sequence<String>, part2: Boolean): String {
    val grid = inputLines
        .flatMapIndexed { row, line ->
            line.mapIndexed { col, char -> Antenna(Vec2(row, col), char) }
        }
        .toList()

    val bounds: Vec2 = grid
        .map { a -> a.pos }
        .let { it.maxOf { (row, _) -> row } to it.maxOf { (_, col) -> col } }

    return grid
        .filter { antenna -> antenna.frequency != '.' }
        .antiNodesForAllFrequencies(part2, bounds)
        .toSet()
        .size
        .toString()

}
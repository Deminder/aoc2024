package com.github.deminder

import com.github.deminder.shared.Grid
import com.github.deminder.shared.GridPos
import com.github.deminder.shared.height
import com.github.deminder.shared.width

private typealias CharGrid = Grid<Char>

object Day04 : Day {

    private fun CharGrid.rows(): List<List<GridPos>> {
        return (0..<this.height())
            .map { row -> (0..<this.width()).map { row to it } }
    }

    private fun CharGrid.columns(): List<List<GridPos>> {
        return (0..<this.width())
            .map { col -> (0..<this.height()).map { it to col } }
    }

    private fun CharGrid.diagonals(): List<List<GridPos>> {
        return (0..<(this.width() + this.height() - 1))
            .map { endRow ->
                (0..endRow)
                    .map { endRow - it to it }
                    .filter { (row, col) -> row in 0..<this.height() && col in 0..<this.width() }
            }
    }

    private fun CharGrid.mirroredDiagonals(): List<List<GridPos>> {
        return this.diagonals()
            .map { it.map { (row, col) -> row to this.width() - 1 - col } }
    }

    private fun CharGrid.readString(positions: List<GridPos>): String {
        return positions
            .map { position -> this[position.first][position.second] }
            .joinToString("")
    }

    private fun solvePart1(charGrid: CharGrid): Int {
        return listOf(
            charGrid.columns(),
            charGrid.rows(),
            charGrid.diagonals(),
            charGrid.mirroredDiagonals()
        )
            .flatten()
            .flatMap { listOf(it, it.reversed()) }
            .sumOf { positions ->
                positions
                    .windowed(4)
                    .map { charGrid.readString(it) }
                    .count { segment -> segment == "XMAS" }
            }
    }

    private fun solvePart2(charGrid: CharGrid): Int {
        val matchCenters: (List<List<GridPos>>) -> List<GridPos> = { diags ->
            diags
                .flatMap { listOf(it, it.reversed()) }
                .flatMap { diag ->
                    diag.windowed(3)
                        .filter { charGrid.readString(it) == "MAS" }
                        .map { (_, center, _) -> center }
                }
        }
        return matchCenters(charGrid.diagonals())
            .intersect(matchCenters(charGrid.mirroredDiagonals()).toSet())
            .count()
    }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val charGrid: CharGrid = inputLines
            .map { line -> line.toCharArray().toList() }
            .toList()

        return if (part2) {
            solvePart2(charGrid)
        } else {
            solvePart1(charGrid)
        }.toString()
    }
}


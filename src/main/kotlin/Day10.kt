package com.github.deminder

import com.github.deminder.Day10.countTrailEndings
import com.github.deminder.shared.*

typealias EndGrid = Grid<List<GridPos>>

object Day10 : Day {


    private fun Grid<Int>.nextHikingPositions(pos: Vec2): List<Vec2> =
        this.getByPos(pos).let { elevation ->
            Direction.entries
                .map { pos.move(it) }
                .filter { it inBounds this.bounds() }
                .filter { this.getByPos(it) == elevation + 1 }
        }

    private fun Grid<Int>.countTrailEndings(trailStart: Vec2, endGrid: EndGrid): EndGrid =
        if (this.getByPos(trailStart) == 9)
        // Mark the ending of a trail
            endGrid.copyWithValueSet(listOf(trailStart), trailStart)
        else
            nextHikingPositions(trailStart)
                .fold(endGrid) { curEndCountGrid, nextPosition ->
                    curEndCountGrid.getByPos(nextPosition).let {
                        if (it.isEmpty()) this.countTrailEndings(nextPosition, curEndCountGrid) else curEndCountGrid
                    }
                }.let { finalEndGrid ->
                    // Join the endings from all hiking directions
                    finalEndGrid.copyWithValueSet(
                        nextHikingPositions(trailStart)
                            .map { finalEndGrid.getByPos(it) }
                            .fold(emptyList()) { a, b -> a + b },
                        trailStart
                    )
                }

    private fun startingPositions(numberGrid: Grid<Int>): List<GridPos> =
        numberGrid
            .positions()
            .filter { (row, col) -> numberGrid[row][col] == 0 }

    private fun findAllEnds(numberGrid: Grid<Int>): EndGrid =
        startingPositions(numberGrid)
            .fold(numberGrid.mapGrid { emptyList() }) { endings, startPos ->
                numberGrid.countTrailEndings(startPos, endings)
            }

    private fun findAllEndingsByStartingPosition(numberGrid: Grid<Int>): List<List<GridPos>> = findAllEnds(numberGrid)
        .let { finalEndCountGrid ->

            startingPositions(numberGrid).map { finalEndCountGrid.getByPos(it) }
        }

    // Collect the number of endings from all starting positions
    fun ratesOfTrailStarts(numberGrid: Grid<Int>): List<Int> =
        findAllEndingsByStartingPosition(numberGrid).map { it.size }


    // Collect the number of unique endings from all starting positions
    fun scoresOfTrailStarts(numberGrid: Grid<Int>): List<Int> =
        findAllEndingsByStartingPosition(numberGrid).map { it.toSet().size }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val numberGrid = parse(inputLines)

        return (if (part2)
            ratesOfTrailStarts(numberGrid)
        else
            scoresOfTrailStarts(numberGrid))
            .sum()
            .toString()
    }

    fun parse(lines: Sequence<String>): Grid<Int> =
        lines.map { line -> line.trim().map { char -> if (char.isDigit()) char.digitToInt() else 1000 } }.toList()


}


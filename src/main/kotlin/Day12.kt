package com.github.deminder

import com.github.deminder.shared.*


typealias Region = Set<GridPos>

object Day12 : Day {


    private fun Grid<Char>.findNeighbors(tile: GridPos) = this.getByPos(tile).let { tileValue ->
        Direction.entries
            .asSequence()
            .map { tile.move(it) }
            .filter { this.includesPos(it) && tileValue == this.getByPos(it) }
    }

    data class FindRegionState(
        val frontier: Set<GridPos>,
        val visited: Set<GridPos>,
    )

    private fun Grid<Char>.findRegion(pos: GridPos) =
        generateSequence(FindRegionState(setOf(pos), setOf(pos))) { state ->
            state.frontier
                .asSequence()
                .flatMap { this.findNeighbors(it) }
                .filter { it !in state.frontier }
                .filter { it !in state.visited }
                .toSet()
                .let { if (it.isEmpty()) null else state.copy(frontier = it, visited = state.visited + it) }
        }.last().visited


    private fun Grid<Char>.findAllRegions(): List<Region> = positions()
        .asSequence()
        .runningFold(null as Region? to emptySet<GridPos>()) { (_, visited), pos ->
            if (pos in visited)
                null to visited
            else
                this.findRegion(pos).let {
                    it to visited.plus(it)
                }
        }
        .map { (region, _) -> region }
        .filterNotNull()
        .toList()


    private fun Region.measurePerimeterAtPos(pos: GridPos) = Direction
        .entries
        .map { pos.move(it) }
        .count { it !in this }

    private fun Region.measurePerimeter() = sumOf {
        measurePerimeterAtPos(it)
    }

    private fun Region.measureSidesAtPos(pos: GridPos) = Direction
        .entries
        .count { direction ->
            pos.move(direction) !in this &&
                    // Count if the side is ending
                    pos.move(direction.right())
                        .let { sideContinueTile -> sideContinueTile.move(direction) in this || sideContinueTile !in this }
        }

    private fun Region.countSides() = sumOf {
        measureSidesAtPos(it)
    }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .findAllRegions()
            .sumOf { it.size * (if (part2) it.countSides() else it.measurePerimeter()) }
            .toString()


    private fun parse(inputLines: Sequence<String>): Grid<Char> =
        inputLines.map { line -> line.map { it } }.toList()


}
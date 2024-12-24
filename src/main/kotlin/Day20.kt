package com.github.deminder

import com.github.deminder.shared.*
import kotlin.math.absoluteValue

private typealias Cheat = Pair<GridPos, GridPos>

object Day20 : Day {

    enum class Tile {
        WALL,
        AIR,
        START,
        END
    }

    private fun trackPositions(raceTrack: Grid<Tile>) = raceTrack
        .positions()
        .find { pos -> raceTrack.getByPos(pos) == Tile.START }!!
        .let { startPosition ->
            generateSequence(startPosition to startPosition) { (pos, prev) ->
                if (raceTrack.getByPos(pos) == Tile.END)
                    null
                else
                    Direction.entries
                        .map { pos.move(it) }
                        .filter { it != prev }
                        .find { raceTrack.getByPos(it) != Tile.WALL }!! to pos
            }
        }
        .map { (pos, _) -> pos }
        .withIndex()
        .associate { (index, pos) -> pos to index }

    private fun cheatOptions(start: GridPos, trackPositions: Map<GridPos, Int>, radius: Int) = pointsInDiamond(radius)
        .map { pos -> start + pos }
        .filter { end -> end in trackPositions }
        .map { end -> start to end }

    private fun savedPicoSecondsByCheat(cheat: Cheat, trackPositions: Map<GridPos, Int>) = cheat.let { (start, end) ->
        trackPositions[end]!! - trackPositions[start]!! - (end - start).let { (x, y) -> x.absoluteValue + y.absoluteValue }
    }

    fun countCheats(raceTrack: Grid<Tile>, part2: Boolean): Map<Int, Int> = trackPositions(raceTrack)
        .let { track ->
            track.keys
                .asSequence()
                .flatMap { pos -> cheatOptions(pos, track, if (part2) 20 else 2) }
                .map { cheat -> savedPicoSecondsByCheat(cheat, track) }
                .groupingBy { it }
                .eachCount()
        }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        countCheats(parse(inputLines), part2)
            .entries
            .filter { (savedPicoSeconds, _) -> savedPicoSeconds >= 100 }
            .sumOf { (_, count) -> count }
            .toString()


    fun parse(inputLines: Sequence<String>): Grid<Tile> = inputLines.map { line ->
        line.map {
            when (it) {
                '.' -> Tile.AIR
                'S' -> Tile.START
                'E' -> Tile.END
                else -> Tile.WALL
            }
        }
    }.toList()


}



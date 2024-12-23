package com.github.deminder

import com.github.deminder.shared.*

object Day18 : Day {

    var SPACE = Vec2(70, 70)
    var STEPS = 1024

    private fun show(corrupted: Set<Vec2>, steps: Set<Vec2>) =
        (0..SPACE.first).joinToString("\n") { row ->
            (0..SPACE.second).joinToString("") { col ->
                (col to row).let { pos ->
                    if (pos in corrupted) "#" else {
                        if (pos in steps) "O" else "."
                    }
                }
            }
        }

    private fun Set<Vec2>.minimumSteps() = shortestPath(
        Vec2(0, 0),
        { pos ->
            Direction.entries.asSequence()
                .map { pos.move(it) }
                .filter { it !in this }
                .filter { it inBounds SPACE }
                .map { 1 to it }
        },
        { pos -> pos == SPACE },
    )
        .let { if (it.hasFoundTarget() && VERBOSE) println(show(this, it.nodesOfShortestPathToTarget())); it }
        .let { if (it.hasFoundTarget()) it.minTargetDistance() else null }


    private fun List<Vec2>.findBlockingStone() =
        this.indices.toList().binarySearch { endIndex ->
            this.subList(0, endIndex + 1)
                .toSet()
                .minimumSteps()
                .let { if (it == null) 1 else -1 }
                .let { if (VERBOSE) println("Index: $endIndex ${this[endIndex]} ($it)"); it }
        }
            .let { this[-it-1] }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .let { falls ->
                if (part2) {
                    falls.findBlockingStone()
                        .let { (x, y) -> "${x},${y}" }
                } else {
                    falls.take(STEPS)
                        .toSet()
                        .minimumSteps()!!
                        .toString()
                }
            }


    private fun parse(inputLines: Sequence<String>) = inputLines
        .map { line -> line.split(",").map { it.toInt() } }
        .map { (x, y) -> Vec2(x, y) }
        .toList()


}



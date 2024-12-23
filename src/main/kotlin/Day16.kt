package com.github.deminder

import com.github.deminder.shared.*

object Day16 : Day {

    data class Maze(
        val wallGrid: Grid<Boolean>,
    ) {
        private fun start() = wallGrid.height() - 2 to 1
        private fun end() = 1 to wallGrid.width() - 2

        private fun isWall(pos: Vec2) =
            wallGrid.getByPos(pos)

        private fun findShortestPathsFromStartToEnd() = shortestPath(
            MazeNode(start(), Direction.RIGHT),
            { node -> node.weightedEdges().filterNot { (_, n) -> isWall(n.position) } },
            { it.position == end() }
        )

        private fun show(path: Set<MazeNode>) =
            wallGrid.mapGridIndexed { pos, wall ->
                if (wall)
                    "#"
                else
                    Direction.entries.count {
                        MazeNode(pos, it) in path
                    }.let {
                        if (it == 0)
                            "."
                        else
                            "O"
                    }
            }.joinToString("\n") { it.joinToString("") }

        fun countSitPositions() = findShortestPathsFromStartToEnd()
            .nodesOfAllShortestPathsToTarget()
            .let { if (VERBOSE) println(show(it)); it }
            .map { it.position }
            .toSet()
            .size

        fun lowestPoints() = findShortestPathsFromStartToEnd().minTargetDistance()
    }

    data class MazeNode(
        val position: Vec2,
        val orientation: Direction,
    ) {

        private fun forward() = copy(
            position = position.move(orientation),
        )

        private fun turnTo(direction: Direction) = copy(
            orientation = direction,
        )

        fun weightedEdges() = sequenceOf(
            1 to forward(),
            1000 to turnTo(orientation.left()),
            1000 to turnTo(orientation.right())
        )

    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .let { maze ->
                if (part2)
                    maze.countSitPositions()
                else
                    maze.lowestPoints()
            }
            .toString()


    private fun parse(inputLines: Sequence<String>) = Maze(
        wallGrid = inputLines
            .map { line -> line.map { it == '#' } }
            .toList()
    )


}



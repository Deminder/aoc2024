package com.github.deminder

import com.github.deminder.shared.VERBOSE
import com.github.deminder.shared.mapAsync
import java.util.*

private typealias Node = String
private typealias Edges = Map<Node, Set<Node>>

object Day23 : Day {

    private fun Edges.pairs() = asSequence()
        .flatMap { (first, seconds) -> seconds.map { if (first < it) first to it else it to first } }
        .toSet()

    private fun Edges.threeCliques(): Set<SortedSet<Node>> = pairs()
        .flatMap { (a, b) -> this[a]!!.intersect(this[b]!!).map { c -> sortedSetOf(a, b, c) } }
        .toSet()

    private fun Edges.growCliques(cliques: Set<SortedSet<Node>>, fromSize: Int) = entries
        .filter { (_, neighbors) -> neighbors.size >= fromSize }
        .mapAsync { (node, neighbors) ->
            cliques
                .filter { neighbors.containsAll(it) }
                .map { it + node }
                .map { it.toSortedSet() }
        }
        .flatten()
        .toSet()


    private fun Edges.cliques() =
        generateSequence(threeCliques() to 3) { (cliques, size) ->
            if (VERBOSE) println(cliques.size)
            growCliques(cliques, size) to (size + 1)
        }
            .map { (cliques, _) -> cliques }
            .takeWhile { it.isNotEmpty() }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .let { edges ->
                if (part2)
                    edges.cliques()
                        .last()
                        .first()
                        .joinToString(",")
                else
                    edges.threeCliques()
                        .count { nodes -> nodes.any { it.startsWith("t") } }
            }
            .toString()


    private fun parse(inputLines: Sequence<String>): Edges = inputLines.flatMap { line ->
        line.split("-").let { (a, b) -> sequenceOf(a to setOf(b), b to setOf(a)) }
    }
        .groupingBy { (a, _) -> a }
        .aggregate { _, neighbors: Set<String>?, (_, neighbor), _ -> (neighbors ?: emptySet()) + neighbor }
}
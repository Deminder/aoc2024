package shared

import com.github.deminder.shared.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class SharedTest {


    @Test
    fun `test points in diamond with radius 1`() {
        val points = pointsInDiamond(1).toSet()

        val expectedPoints = Direction.entries
            .map { Vec2(0, 0).move(it) }
            .toSet()
        assertEquals(expectedPoints, points)
    }

    @Test
    fun `test points in diamond with radius 2`() {
        val points = pointsInDiamond(2).toSet()

        val expectedPoints = Direction.entries
            .map { Vec2(0, 0).move(it) }
            .flatMap { pos ->
                Direction.entries
                    .map { pos.move(it) }
                    .plus(pos)
            }
            .toSet()
            .minus(Vec2(0, 0))
        assertEquals(expectedPoints, points)
    }

    @Test
    fun `test points in diamond with radius 3`() {
        val points = pointsInDiamond(3).toSet()

        val expectedPoints = Direction.entries
            .map { Vec2(0, 0).move(it) }
            .flatMap { pos ->
                Direction.entries
                    .map { pos.move(it) }
                    .plus(pos)
                    .flatMap { pos2 ->
                        Direction.entries
                            .map { pos2.move(it) }
                            .plus(pos2)
                    }
            }
            .toSet()
            .minus(Vec2(0, 0))
        assertEquals(expectedPoints, points)
    }

    @Test
    fun `test points in diamond only occur once`() {
        val counts = pointsInDiamond(20)
            .groupingBy { it }
            .eachCount()

        assertAll(counts.map { (pos, count) ->
            { assertEquals(1, count, "Point $pos should only occur once") }
        })
    }

    @Test
    fun `test permutations`() {
        val perms = listOf(1, 2, 3).permutations().toList()

        assertEquals(
            listOf(
                listOf(1, 2, 3),
                listOf(1, 3, 2),
                listOf(2, 1, 3),
                listOf(2, 3, 1),
                listOf(3, 1, 2),
                listOf(3, 2, 1)
            ), perms
        )
    }

}
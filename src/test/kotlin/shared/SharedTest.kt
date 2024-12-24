package shared

import com.github.deminder.shared.Direction
import com.github.deminder.shared.Vec2
import com.github.deminder.shared.move
import com.github.deminder.shared.pointsInDiamond
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

}
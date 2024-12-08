import com.github.deminder.Day05
import com.github.deminder.Day06
import com.github.deminder.shared.Direction
import com.github.deminder.shared.VERBOSE
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals


class Day06Test {

    private val day = Day06

    private val inputString = """
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
    """.trimIndent();

    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("41", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(inputString.lineSequence(), true)

        assertEquals("6", result)
    }

    @Nested
    inner class BoardTest {

        private val originalBoard = Day06.Board.parse(inputString.lineSequence())

        @TestFactory
        fun `test guard walk positions`() = listOf(
            Direction.UP to 6,
            Direction.RIGHT to 7,
            Direction.DOWN to 5,
            Direction.LEFT to 3,
        ).map { (direction, expectedLineLength) ->
            DynamicTest.dynamicTest("should walk $direction for $expectedLineLength steps") {
                val board = originalBoard.copy(guard = originalBoard.guard.copy(direction = direction))

                val result = board.guard.positionsUntilLeavingOrNextObstruction(board.obstructions)

                assertAll(
                    { assertEquals(board.guard.pos, result[0]) },
                    { assertEquals(board.guard.step().pos, result[1]) },
                    { assertEquals(board.guard.step().step().pos, result[2]) },
                    { assertEquals(expectedLineLength, result.size) }
                )
            }
        }

    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            VERBOSE = true
        }
    }
}
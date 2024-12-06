import com.github.deminder.solveDay06
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day06Test {

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
        val result = solveDay06(inputString.lineSequence(), false)

        assertEquals("41", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = solveDay06(inputString.lineSequence(), true)

        assertEquals("6", result)
    }
}
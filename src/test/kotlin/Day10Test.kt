import com.github.deminder.Day09
import com.github.deminder.Day10
import com.github.deminder.shared.VERBOSE
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class Day10Test {

    private val day = Day10

    private val inputString = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent()


    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("36", result)
    }

    @Test
    fun `test trailhead scores part 1`() {
        val numberGrid = day.parse(inputString.lineSequence())

        val scores = day.scoresOfTrailStarts(numberGrid)

        assertAll(
            { assertEquals(5, scores[0]) },
            { assertEquals(6, scores[1]) },
            { assertEquals(5, scores[2]) },
            { assertEquals(3, scores[3]) },
            { assertEquals(1, scores[4]) },
            { assertEquals(3, scores[5]) },
            { assertEquals(5, scores[6]) },
            { assertEquals(3, scores[7]) },
            { assertEquals(5, scores[8]) }

        )
    }

    @Test
    fun `test simple part 1`() {
        val result = Day10.solve(
            """
            ..90..9
            ...1.98
            ...2..7
            6543456
            765.987
            876....
            987....
        """.trimIndent().lineSequence(), false
        )

        assertEquals("4", result)
    }


    @Test
    fun `test solve part 2`() {
        val result = day.solve(
            inputString.lineSequence(), true
        )

        assertEquals("81", result)
    }

    @Test
    fun `test trailhead scores part 2`() {
        val numberGrid = day.parse(inputString.lineSequence())

        val scores = day.ratesOfTrailStarts(numberGrid)

        assertAll(
            { assertEquals(20, scores[0]) },
            { assertEquals(24, scores[1]) },
            { assertEquals(10, scores[2]) },
            { assertEquals(4, scores[3]) },
            { assertEquals(1, scores[4]) },
            { assertEquals(4, scores[5]) },
            { assertEquals(5, scores[6]) },
            { assertEquals(8, scores[7]) },
            { assertEquals(5, scores[8]) }

        )
    }


    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            VERBOSE = true
        }
    }

}
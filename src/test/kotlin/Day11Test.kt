import com.github.deminder.Day11
import com.github.deminder.shared.Direction
import com.github.deminder.shared.VERBOSE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Day11Test {

    private val day = Day11

    private val inputString = """
        125 17
    """.trimIndent()


    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("55312", result)
    }

    @TestFactory
    fun `test blink stones`() = listOf(
        1 to 3,
        2 to 4,
        3 to 5,
        4 to 9,
        5 to 13,
        6 to 22,
    ).map { (blinks, expectedStoneCount) ->
        DynamicTest.dynamicTest("should have $expectedStoneCount stones after $blinks blinks") {
            val result = day.blink(inputString.lineSequence(), blinks)

            assertEquals(expectedStoneCount.toLong(), result)
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
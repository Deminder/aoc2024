import com.github.deminder.Day22
import com.github.deminder.shared.VERBOSE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Day22Test {

    private val day = Day22

    private val inputString = """
        1
        2
        3
        2024
    """.trimIndent()


    @TestFactory
    fun `test 2000th secret number`() = listOf(
        1L to 8685429L,
        10L to 4700978L,
        100L to 15273692L,
        2024L to 8667524L,
    ).map { (secretNumber, expected2000thNumber) ->
        dynamicTest("should be $expected2000thNumber for $secretNumber") {
            val result = day.evolve2000th(secretNumber).last().value

            assertEquals(expected2000thNumber, result)
        }
    }



    @Test
    fun `test solve part 2`() {
        val result = day.solve(inputString.lineSequence(), true)

        assertEquals("23", result)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            VERBOSE = true
        }
    }

}

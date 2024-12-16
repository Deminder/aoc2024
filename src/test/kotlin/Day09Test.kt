import com.github.deminder.Day09
import com.github.deminder.shared.VERBOSE
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class Day09Test {

    private val day = Day09

    private val inputString = "2333133121414131402"

    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("1928", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(
            inputString.lineSequence(), true
        )

        assertEquals("2858", result)
    }


    @Nested
    inner class File {

        @TestFactory
        fun `test checksum`() = listOf(
            Day09.File(0, 2, 0) to 0,
            Day09.File(9, 2, 2) to (2 * 9) + (3 * 9),
            Day09.File(8, 1, 4) to (4 * 8),
            Day09.File(1, 3, 5) to (5 * 1) + (6 * 1) + (7 * 1),
        ).map { (file, expectedCheckSum) ->
            DynamicTest.dynamicTest("should have checksum $expectedCheckSum for file $file") {

                val result = file.checkSum()

                assertEquals(expectedCheckSum.toLong(), result)
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
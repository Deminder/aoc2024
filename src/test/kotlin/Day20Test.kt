import com.github.deminder.Day20
import com.github.deminder.shared.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest

class Day20Test {

    private val day = Day20

    private val inputString = """
        ###############
        #...#...#.....#
        #.#.#.#.#.###.#
        #S#...#.#.#...#
        #######.#.#.###
        #######.#.#...#
        #######.#.###.#
        ###..E#...#...#
        ###.#######.###
        #...###...#...#
        #.#####.#.###.#
        #.#...#.#.#...#
        #.#.#.#.#.#.###
        #...#...#...###
        ###############
    """.trimIndent()


    @TestFactory
    fun `test cheat counts part 1`() = listOf(
        14 to 2,
        14 to 4,
        2 to 6,
        4 to 8,
        2 to 10,
        3 to 12,
        1 to 20,
        1 to 36,
        1 to 38,
        1 to 40,
        1 to 64
    ).map { (cheatCount, savedPicoSeconds) ->
        dynamicTest("should have $cheatCount cheats for $savedPicoSeconds picoseconds") {
            val raceGrid = day.parse(inputString.lineSequence())

            val counts = day.countCheats(raceGrid, false)

            assertEquals(cheatCount, counts[savedPicoSeconds])
        }
    }

    @TestFactory
    fun `test cheat counts part 2`() = listOf(
        32 to 50,
        31 to 52,
        29 to 54,
        39 to 56,
        25 to 58,
        23 to 60,
        20 to 62,
        19 to 64,
        12 to 66,
        14 to 68,
        12 to 70,
        22 to 72,
        4 to 74,
        3 to 76
    ).map { (cheatCount, savedPicoSeconds) ->
        dynamicTest("should have $cheatCount cheats for $savedPicoSeconds picoseconds") {
            val raceGrid = day.parse(inputString.lineSequence())

            val counts = day.countCheats(raceGrid, true)

            assertEquals(cheatCount, counts[savedPicoSeconds])
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
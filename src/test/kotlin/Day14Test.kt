import com.github.deminder.Day14
import com.github.deminder.shared.VERBOSE
import com.github.deminder.shared.Vec2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Day14Test {

    private val day = Day14

    private val inputString = """
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
    """.trimIndent()


    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("12", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(inputString.lineSequence(), true)

        assertEquals("17", result)
    }

    @Test
    fun `test find continuous line`() {
        val result = day.findContinuousLine(setOf(
            Vec2(0, 0),
            Vec2(0, 1),
            Vec2(0, 2),
            Vec2(0, 4),
        ))

        assertEquals(setOf(Vec2(0, 0), Vec2(0, 1), Vec2(0, 2)), result)
    }


    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            VERBOSE = true
            Day14.ROOM_SIZE = Vec2(11, 7)
        }
    }

}
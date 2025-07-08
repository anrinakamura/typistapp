package app.typist

import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull

class ComposeAppCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun color_convertINTtoRGB() {
        val aRGB = 0;
        val actual = ColorUtility.convertINTtoRGB(aRGB)
        val expected: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)

        assertContentEquals(expected, actual)
    }
}
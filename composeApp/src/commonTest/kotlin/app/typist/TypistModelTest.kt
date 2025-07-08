package app.typist

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class TypistModelTest {

    private lateinit var model: TypistModel

    @BeforeTest
    fun setup() {
        val chars = listOf('a', 'b', 'c')

        model = TypistModel(chars)
    }

    @Test
    fun typistModel_constructor() {
        val aFile = File("example.txt")
        val aModel = TypistModel(aFile)
        assertNotNull(aModel)
        println("construct typist model")
    }
}
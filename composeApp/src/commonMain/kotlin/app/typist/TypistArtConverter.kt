package app.typist

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import typistapp.composeapp.generated.resources.Res
import kotlin.math.abs
import kotlin.math.sqrt

const val DOUBLE_ALMOST_ZERO = 1e-12
const val NUM_OF_CANDIDATES = 16

@Serializable
data class TypesetElement(
    val character: String,
    val luminance: Double,
    val characteristic: List<Double>
)

data class PictureElement(
    var luminance: Double,
    val characteristic: List<Double>
)

class TypistArtConverter(
    // Sorted by luminance.
    private val typesetElements: List<TypesetElement>
) {

    fun convert(anImage: ImageBitmap): String {
        // val i = imageResource(resource = Res.drawable.resized_monalisa)
        // val anImage = imageBytes.decodeToImageBitmap()

        val imageWidth = anImage.width;
        val imageHeight = anImage.height;

        val columns = 32
        val size = imageWidth / columns
        val lines = imageHeight / size

        val pixelMap = anImage.toPixelMap()

        // Separate image bytes into image elements.
        val pictureElements = mutableListOf<PictureElement>()

        for (y in 0 until lines) {
            for (x in 0 until columns) {
                for (offsetY in 0..size) {
                    val colors = mutableListOf<Color>()
                    for (offsetX in 0..size) {
                        val color = pixelMap[x * size + offsetX, y * size + offsetY]
                        colors.add(color)
                    }

                    val characteristic = colors.map { it.toLuminance() }
                    val luminance = characteristic.average()
                    pictureElements.add(PictureElement(luminance, characteristic))
                    println("picture element ($x, $y): $luminance")
                }
            }
        }

        // Normalize picture elements.
        pictureElements.normalized()

        // Search the most similar typeset for each elements.
        val typistArtElements = pictureElements.toTypistArt()

        val result = mutableListOf<String>()
        val aStringBuilder = StringBuilder()
        typistArtElements.forEachIndexed { i, e ->
            aStringBuilder.append(e.character)

            // Insert new line
            if ((i + 1) % columns == 0) {
                result.add(aStringBuilder.toString())
                aStringBuilder.clear()
            }
        }
        if (result.isNotEmpty()) {
            result.add(aStringBuilder.toString())
        }

        for (aString in result) {
            println(aString)
        }

        // TODO: update
        return "typist-art".toString()
    }

    private fun Color.toLuminance(): Double {
        val yuv = this.toYuv()

        // Returns the luminance (Y component) from a YUV value.
        return yuv[0]
    }

    private fun Color.toYuv(): DoubleArray {
        val y = 0.299 * this.red + 0.587 * this.green + 0.114 * this.blue
        val u = -0.169 * this.red - 0.331 * this.green + 0.500 * this.blue
        val v = 0.500 * this.red - 0.419 * this.green - 0.081 * this.blue

        return doubleArrayOf(y, u, v)
    }

    private fun List<PictureElement>.normalized() {
        var min = Double.MAX_VALUE
        var max = Double.MIN_VALUE

        for (element in this) {
            if (element.luminance < min) {
                min = element.luminance
            }
            if (element.luminance > max) {
                max = element.luminance
            }
        }

        this.map { it.normalized(min, max) }
        return
    }

    private fun PictureElement.normalized(min: Double, max: Double) {
        if (max - min < DOUBLE_ALMOST_ZERO) {
            this.characteristic.map { 0.0 }
            this.luminance = 0.0
        }

        this.characteristic.map { (it - min) / (max - min) }
        this.luminance = (this.luminance - min) / (max - min)

        return
    }



    private fun List<PictureElement>.toTypistArt(): List<TypesetElement> {
        val elements = this.map { searchTypesetElement(it) }
        return elements
    }

    private fun searchTypesetElement(pictureElement: PictureElement): TypesetElement {
        // STEP 1: find the index of the character with the most similar average luminance.
        val index = closestLuminanceIndex(pictureElement.luminance)

        // STEP 2: create a slice of candidates around that index for a more detailed search.
        val from = (index - NUM_OF_CANDIDATES / 2).coerceAtLeast(0)
        val to = (from + NUM_OF_CANDIDATES).coerceAtMost(typesetElements.size)
        val candidates = typesetElements.subList(from, to)

        // TODO: update to use default element
        if (candidates.isEmpty()) {
            return typesetElements.getOrNull(index) ?: TypesetElement("　", 0.0, emptyList())
        }

        // STEP 3: from the candidates, find the best match using pixel-by-pixel correlation.
        return bestMatchElement(pictureElement, candidates)
    }

    private fun closestLuminanceIndex(target: Double): Int {
        // TODO: update to use converter
        val targetElement = TypesetElement("", target, emptyList())
        val index = typesetElements.binarySearch(targetElement, compareBy { it.luminance })

        return when {
            index >= 0 -> index
            // binarySearch returns -(index + 1) if the element is not found.
            index <= -typesetElements.size -> typesetElements.size - 1
            else -> {
                // TODO: fix?
                // val i = -(index + 1)
                // val left = typesetElements.getOrNull(index - 1)
                // val right = typesetElements.getOrNull(index + 1)
                -index - 1
            }
        }
    }

    private fun bestMatchElement(target: PictureElement, candidates: List<TypesetElement>): TypesetElement {
        var max = -1.0
        var best: TypesetElement? = null

        for (candidate in candidates) {
            val result = correlation(target.characteristic, candidate.characteristic)
            // Update values if result is not null
            result?.let { it ->
                if (it > max) {
                    max = it
                    best = candidate
                }
            }
        }

        val default = TypesetElement("　", 0.0, emptyList())
        return best ?:default
    }

    private fun correlation(xValues: List<Double>, yValues: List<Double>): Double? {
        if (xValues.size != yValues.size || xValues.isEmpty()) {
            return null
        }

        val n = xValues.size
        val meanX = xValues.sum() / n
        val meanY = yValues.sum() / n

        var numerator = 0.0
        var denX = 0.0
        var denY = 0.0

        for (i in 0 until n) {
            val diffX = xValues[i] - meanX
            val diffY = yValues[i] - meanY
            numerator += diffX * diffY
            denX += diffX * diffX
            denY += diffY * diffY
        }

        val denominator = sqrt(denX) * sqrt(denY)
        if (abs(denominator) < DOUBLE_ALMOST_ZERO) {
            val isDenXZero = abs(denX) < DOUBLE_ALMOST_ZERO
            val isDenYZero = abs(denY) < DOUBLE_ALMOST_ZERO
            val areMeansEqual = abs(meanX - meanY) < DOUBLE_ALMOST_ZERO

            return when {
                isDenXZero && isDenYZero && areMeansEqual -> 1.0
                else -> 0.0
            }
        }

        return numerator / denominator
    }
}

suspend fun readResourceFile(): List<TypesetElement> {
    // val jsonString = Res.readBytes("files/sample_element.json").decodeToString()
    val jsonString = Res.readBytes("files/typeset_elements.json").decodeToString()
    return Json.decodeFromString<List<TypesetElement>>(jsonString)
}

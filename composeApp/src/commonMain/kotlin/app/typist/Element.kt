package app.typist

//import utility.ColorUtility
import java.awt.Point
import java.awt.image.BufferedImage

/**
 * A class to hold and manipulate information such as an image or character,
 * along with its associated features and position.
 *
 * @property piece A piece of the source image.
 * @property character The corresponding character (can be null).
 * @property location The position (x, y) (can be null).
 */
class Element(
    val piece: BufferedImage,
    var character: Char?,
    var location: Point?
) {

    /** The features of the image (luminance of each pixel). */
    var characteristics: List<Double> = emptyList()

    /** The average luminance of the image piece. */
    var luminance: Double = 0.0

    /** The distance related to the correlation coefficient. */
    var distance: Double? = null

    /** The correlation coefficient. */
    var coefficient: Double? = null

    /** Secondary constructor for an instance with an image and a character. */
    constructor(anImage: BufferedImage, aCharacter: Char) : this(anImage, aCharacter, null)

    /** Secondary constructor for an instance with an image and a position. */
    constructor(anImage: BufferedImage, xValue: Int, yValue: Int) : this(anImage, null, Point(xValue, yValue))

    /** The x-coordinate, derived from the location. */
    val x: Int?
        get() = location?.x

    /** The y-coordinate, derived from the location. */
    val y: Int?
        get() = location?.y

//    init {
//        val width = piece.width
//        val totalPixels = width * piece.height
//
//        // Calculate features (luminance of each pixel) and store them.
//        val characteristicsList = (0 until totalPixels).map { i ->
//            val px = i % width
//            val py = i / width
//            ColorUtility.luminanceFromRGB(piece.getRGB(px, py))
//        }
//        this.characteristics = characteristicsList
//
//        // Calculate the average luminance and store it.
//        this.luminance = characteristicsList.average()
//    }

    /**
     * Normalizes the luminance and features based on a given range.
     *
     * @param minimumLuminance The minimum luminance for normalization.
     * @param maximumLuminance The maximum luminance for normalization.
     */
    fun normalize(minimumLuminance: Double, maximumLuminance: Double) {
        val range = maximumLuminance - minimumLuminance

        if (range != 0.0) {
            // Normalize each pixel's luminance.
            this.characteristics = this.characteristics.map { lum -> (lum - minimumLuminance) / range }
            // Normalize the average luminance.
            this.luminance = (this.luminance - minimumLuminance) / range
        } else {
            // If the range is zero, set all values to zero to avoid division by zero.
            this.characteristics = this.characteristics.map { 0.0 }
            this.luminance = 0.0
        }
    }

    /**
     * Sets the location using x and y coordinates.
     */
    fun setLocation(x: Int, y: Int) {
        this.location = Point(x, y)
    }

//    /**
//     * Returns the string representation of this instance.
//     */
//    override fun toString(): String {
//        val locationStr = location?.let { "(${it.x}, ${it.y})" } ?: "null"
//        val coefficientStr = coefficient?.let { "%.4f".format(it) } ?: "null"
//        val distanceStr = distance?.let { "%.4f".format(it) } ?: "null"
//
//        return "Element(character=$character, location=$locationStr, luminance=%.4f".format(luminance) +
//                ", coefficient=$coefficientStr, distance=$distanceStr)"
//    }
}

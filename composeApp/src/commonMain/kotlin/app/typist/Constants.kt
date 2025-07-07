package app.typist

import java.awt.Color
import java.awt.Font

/**
 * Constants used in the typist art application.
 */
object Constants {

    /**
     * Defines the tolerance for accuracy.
     */
    const val ACCURACY: Double = 0.000000000001

    /**
     * The character encoding scheme.
     */
    const val CHARACTER_SET: String = "UTF-8"

    /**
     * The file extension for HTML files.
     */
    const val HTML_EXTENSION: String = ".html"

    /**
     * The image format to be used.
     */
    const val IMAGE_FORMAT: String = "png"

    /**
     * The minimum number of characters for the input length.
     */
    const val MINIMUM_TYPING_LENGTH: Int = 32

    /**
     * The maximum number of characters for the input length.
     */
    const val MAXIMUM_TYPING_LENGTH: Int = 128

    /**
     * The number of candidates to consider.
     */
    const val NUMBER_OF_CANDIDATES: Int = 16

    /**
     * The file extension for PNG files.
     */
    const val PNG_EXTENSION: String = ".png"

    /**
     * The unit of time for the sleep process, in milliseconds.
     */
    const val SLEEP_TICK: Int = 5

    /**
     * The HTML header.
     */
    const val TYPIST_ART_HEADER: String = "<!DOCTYPE html>\n<html lang=\"ja\">\n<body>\n<pre>"

    /**
     * The HTML footer.
     */
    const val TYPIST_ART_FOOTER: String = "</pre>\n</body>\n</html>\n"

    /**
     * The filename for the typist art output.
     */
    const val TYPIST_ART_FILENAME: String = "typistArt"

    /**
     * The name of the font to be used.
     */
    const val FONT_NAME: String = "Noto Sans JP"

    /**
     * The file path for the font file.
     */
    const val FONT_FILE_PATH: String = "./resource/NotoSansJP-Regular.otf"

    /**
     * Font settings for the image data of full-width characters.
     */
    @JvmField
    val IMAGE_FONT: Font = Font(FONT_NAME, Font.BOLD, 18)

    /**
     * The default font size for the image data of full-width characters.
     */
    const val IMAGE_FONT_SIZE: Int = 18

    /**
     * The margin size for the image data of full-width characters.
     */
    const val IMAGE_MARGIN: Int = 1

    /**
     * Used for standard font settings.
     */
    @JvmField
    val DEFAULT_FONT: Font = Font(FONT_NAME, Font.PLAIN, 12)

    /**
     * The default font size.
     */
    const val DEFAULT_FONT_SIZE: Int = 12

    /**
     * The margin size.
     */
    const val MARGIN: Int = 1

    /**
     * The foreground color (text color).
     */
    @JvmField
    val FOREGROUND_COLOR: Color = Color.black

    /**
     * The background color.
     */
    @JvmField
    val BACKGROUND_COLOR: Color = Color.white
}

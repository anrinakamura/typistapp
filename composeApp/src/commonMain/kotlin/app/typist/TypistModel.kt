package app.typist

import java.awt.Font
import java.awt.FontFormatException
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import javax.swing.SwingUtilities
import kotlin.math.abs

/**
 * A class that serves as the Model (M) in MVC. Specializes in data management.
 *
 * @param aFile The text file containing full-width characters.
 */
class TypistModel(private val typesetChars: List<Char>) {

    /** The number of characters per line in the typist art. */
    var typingLength: Int? = null

    /** The original image to be converted into typist art. */
    private var originalImage: BufferedImage? = null

    /** The resized version of the original image, used during the typist art conversion. */
    private var pictureImage: BufferedImage? = null

    /** The size (width and height) of a full-width character. */
    private var typesetSize: Int? = null

    /** The number of columns per line in the typist art. */
    private var numberOfColumns: Int? = null

    /** The number of lines in the typist art. */
    private var numberOfLines: Int? = null

    // /** A collection of full-width characters. */
    // private var typesetChars: List<Char> = emptyList()

    /** A collection of Elements for the full-width characters. */
    private var typesetElements: List<Element> = mutableListOf()

    /** A collection of Elements from the original image. */
    private var pictureElements: MutableList<Element> = mutableListOf()

    /** A field indicating whether the mouse button has been pressed. */
    var mouseButtonPressed: Boolean = false

    /** A collection of elements for the typist art. */
    private var typistArt: List<Element>? = null

    /** The minimum luminance of the entire image. */
    private var minLuminance: Double = 0.0

    /** The maximum luminance of the entire image. */
    private var maxLuminance: Double = 1.0

    /** The current frame index of the animation. */
    private var animationIndex: Int = 0

    // init {
    //     // Read the file of full-width characters.
    //     this.typesetChars = this.readTextFile(aFile)
    // }

    /**
     * Initializes the fields.
     */
    private fun initialize() {
        val currentTypingLength = typingLength ?: return
        val currentOriginalImage = originalImage ?: return

        val width = currentTypingLength * (Constants.DEFAULT_FONT_SIZE + Constants.MARGIN * 2)
        val height = currentOriginalImage.height * width / currentOriginalImage.width

        // Calculate the size of full-width characters and the dimensions (columns and lines) of the typist art.
        this.typesetSize = width / currentTypingLength
        this.numberOfColumns = currentTypingLength
        this.numberOfLines = typesetSize?.let { height / it }

        this.typesetElements = sortElements(generateTypesetElements())
        this.pictureElements = generatePictureElements()
    }

    /**
     * Performs the main processing of the application.
     */
    fun perform() {
        initialize()
        println("[model] Model was successfully initialized!")

        this.typistArt = typistArt()

//        animate {
//            val artString = typistArtString(this.typistArt!!)
//            val fileString = generateTypistArtHtml(artString)
//            FileUtility.open(fileString)
//            sleep(50)
//            dependents.forEach { view ->
//                (view as? TypistView)?.retryScreen()
//            }
//        }
    }

//    /**
//     * Performs the animation.
//     *
//     * @param onFinished A callback to be invoked when the animation finishes.
//     */
//    fun animate(onFinished: () -> Unit) {
//        animationIndex = 0
//
//        // Start the animation in a background thread.
//        Thread {
//            val artSize = typistArt?.size ?: 0
//            while (animationIndex < artSize) {
//                // Execute model change notifications on the Swing Event Dispatch Thread.
//                SwingUtilities.invokeLater { changed() }
//                sleep(Constants.SleepTick)
//                animationIndex++
//            }
//            SwingUtilities.invokeLater(onFinished)
//        }.start()
//    }

//    /**
//     * Waits for a specified amount of time (sleepTick) between animation frames.
//     *
//     * @param sleepTick The time to wait in milliseconds.
//     */
//    protected fun sleep(sleepTick: Int) {
//        try {
//            Thread.sleep(sleepTick.toLong())
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//            // Restore the interrupted status
//            Thread.currentThread().interrupt()
//        }
//    }

//    /**
//     * Notifies dependents (broadcasts an update request) that this object has changed.
//     */
//    fun changed() {
//        val g2d = picture().createGraphics()
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
//
//        val currentArt = typistArt ?: return
//        val cols = numberOfColumns ?: return
//        val size = Constants.DefaultFontSize + Constants.Margin * 2
//        val margin = Constants.Margin
//
//        for (i in 0 until animationIndex) {
//            val element = currentArt[i]
//
//            // Calculate position from the index.
//            val x = (i % cols) * size
//            val y = (i / cols) * size
//
//            // Draw the background.
//            g2d.color = Constants.BackgroundColor
//            g2d.fillRect(x, y, size, size)
//
//            // Draw the full-width character.
//            g2d.color = Constants.ForegroundColor
//            g2d.font = Constants.DefaultFont
//            g2d.drawString(element.character().toString(), x + margin, y + size - margin)
//        }
//
//        g2d.dispose()
//
//        // Broadcast to dependent views that the model has changed (requesting an update).
//        dependents.forEach { it.update() }
//    }

    /**
     * Reads a text file (aFile) of full-width characters.
     *
     * @param aFile The text file.
     * @return A list of full-width characters.
     */
    private fun readTextFile(aFile: File): List<Char> {
        println("[model] loading $aFile...")
        // val lines = try {
            // // Use Kotlin's `useLines` for safe, efficient file reading.
            // aFile.bufferedReader(charset(Constants.CharacterSet)).useLines { it.toList() }
        // } catch (e: IOException) {
        //     e.printStackTrace()
            // throw RuntimeException(e)
        // }
        val lines = aFile.readLines()
        println(lines)
        return lines.joinToString("").toList()
    }

    /**
     * Generates Elements from the image.
     *
     * @return A mutable list of Elements.
     */
    private fun generatePictureElements(): MutableList<Element> {
        val imageSize = Constants.IMAGE_FONT_SIZE + Constants.IMAGE_MARGIN * 2
        val elements = mutableListOf<Element>()
        val currentPictureImage = pictureImage ?: return elements

        for (indexY in 0 until (numberOfLines ?: 0)) {
            for (indexX in 0 until (typingLength ?: 0)) {
                val x = indexX * imageSize
                val y = indexY * imageSize

                val anImage = currentPictureImage.getSubimage(x, y, imageSize, imageSize)
                val anElement: Element = Element(anImage, x, y).apply {
                    normalize(minLuminance, maxLuminance)
                }
                elements.add(anElement)
            }
        }
        return elements
    }

    /**
     * Generates Elements from the collection of full-width characters.
     *
     * @return A list of Elements.
     */
    private fun generateTypesetElements(): List<Element> {
        readFontFile()

        if (checkFont()) {
            println("[model] Font file '${Constants.FONT_NAME}' exists.")
        } else {
            println("[model] Font file '${Constants.FONT_NAME}' doesn't exist.")
        }

        val elements = typesetChars.map { char ->
            val image = generateTypesetImage(char)
            Element(image, char)
        }

        // Find the minimum and maximum luminance, then normalize.
        val minTypesetLuminance = elements.minOfOrNull { it.luminance } ?: 1.0
        val maxTypesetLuminance = elements.maxOfOrNull { it.luminance } ?: 0.0

        println("[model] Character Luminance Range (Raw): [%.4f, %.4f]".format(minTypesetLuminance, maxTypesetLuminance))

        elements.forEach { it.normalize(minTypesetLuminance, maxTypesetLuminance) }

        return elements
    }

    /**
     * Checks if the font is loaded.
     *
     * @return true if the font exists, false otherwise.
     */
    fun checkFont(): Boolean {
        val fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
        return Constants.FONT_NAME in fonts
    }

    /**
     * Loads the font data.
     */
    private fun readFontFile() {
        try {
            val font = Font.createFont(Font.TRUETYPE_FONT, File(Constants.FONT_FILE_PATH))
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)
        } catch (e: Exception) { // FontFormatException | IOException
            e.printStackTrace()
        }
    }

    /**
     * Generates an image of a full-width character (aCharacter).
     *
     * @param aCharacter The full-width character.
     * @return An image with the full-width character drawn on it.
     */
    protected fun generateTypesetImage(aCharacter: Char): BufferedImage {
        val charString = aCharacter.toString()
        val fontSize = Constants.IMAGE_FONT_SIZE
        val margin = Constants.IMAGE_MARGIN
        val imageSize = fontSize + margin * 2

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2d.color = Constants.BACKGROUND_COLOR
        g2d.fillRect(0, 0, imageSize, imageSize)

        g2d.color = Constants.FOREGROUND_COLOR
        g2d.font = Constants.IMAGE_FONT

        val fm = g2d.fontMetrics
        val x = (imageSize - fm.stringWidth(charString)) / 2
        val y = ((imageSize - fm.height) / 2) + fm.ascent

        g2d.drawString(charString, x, y)
        g2d.dispose()

        return image
    }

    /**
     * Generates an HTML file for the typist art.
     *
     * @param aString The typist art string.
     * @return The HTML file path as a string.
     */
    protected fun generateTypistArtHtml(aString: String): String {
        val currentDirectory = System.getProperty("user.dir")
        println("[model] current directory is '$currentDirectory'.")
        val fileString = currentDirectory + File.separator + Constants.TYPIST_ART_FILENAME + Constants.HTML_EXTENSION
        println("[model] HTML file is '$fileString'.")

        val aFile = File(fileString)
        aFile.writeText(Constants.TYPIST_ART_HEADER)
        aFile.appendText(aString)
        aFile.appendText("\n")
        aFile.appendText(Constants.TYPIST_ART_FOOTER)

        // try {
        //     File(fileString).bufferedWriter().use { writer ->
        //         writer.write(Constants.TypistArtHeader)
        //         writer.write(aString)
        //         writer.newLine()
        //         writer.write(Constants.TypistArtFooter)
        //     }
        // } catch (e: IOException) {
        //     e.printStackTrace()
        // }
        return fileString
    }

    /**
     * Sorts a list of Elements by luminance in ascending order and returns the result.
     *
     * @param elements The list of Elements.
     * @return A list sorted by luminance in ascending order.
     */
    protected fun sortElements(elements: List<Element>): List<Element> {
        return elements.sortedBy { it.luminance }
    }

    /**
     * Generates the typist art using a thread pool for parallel processing.
     *
     * @return A list of Elements representing the typist art.
     */
    fun typistArt(): List<Element> {
        val cores = Runtime.getRuntime().availableProcessors()
        println("[model] detected $cores CPU cores. creating thread pool...")
        val executor = Executors.newFixedThreadPool(cores)

        val tasks: List<Callable<Element?>> = pictureElements.map { element ->
            Callable { searchTypesetElement(element) }
        }

        try {
            println("[model] submitting ${tasks.size} tasks to executor...")
            val startTime = System.currentTimeMillis()

            val futureResults = executor.invokeAll(tasks)

            val endTime = System.currentTimeMillis()
            println("[model] all tasks completed in ${endTime - startTime} ms")

            val results: List<Element?> = futureResults.map { future ->
                try {
                    future.get()
                } catch (e: Exception) { // InterruptedException | ExecutionException
                    throw RuntimeException("Failed to get task result", e)
                }
            }
            println("[model] typist-art is successfully generated!")

            // TODO: fix
            return results.filterNotNull()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Thread.currentThread().interrupt()
            return emptyList()
        } finally {
            if (!executor.isShutdown) {
                executor.shutdown()
            }
        }
    }

    /**
     * Finds the full-width character with the highest similarity to the image part (pictureElement)
     * and returns its Element.
     *
     * @param pictureElement The Element of the image part.
     * @return The Element of the most similar full-width character.
     */
    protected fun searchTypesetElement(pictureElement: Element): Element? {
        // Step 1: Get characters with similar average luminance (pruning).
        val index = binarySearch(pictureElement.luminance)
        // val from = max(0, index - Constants.NUMBER_OF_CANDIDATES / 2)
        // val to = min(this.typesetElements.size, from + Constants.NUMBER_OF_CANDIDATES)
        val from = (index - Constants.NUMBER_OF_CANDIDATES / 2).coerceAtLeast(0)
        val to = (from + Constants.NUMBER_OF_CANDIDATES).coerceAtMost(this.typesetElements.size)
        val candidates = this.typesetElements.subList(from, to)

        // Step 2: Select the character with the highest correlation coefficient.
        return candidates.maxByOrNull { element ->
            Correlation(pictureElement.characteristics, element.characteristics).coefficient
        }
    }

    /**
     * Uses binary search to find the full-width character with the average luminance
     * closest to the target luminance and returns its index.
     *
     * @param target The target luminance.
     * @return The index of the full-width character.
     */
    private fun binarySearch(target: Double): Int {
        var low = 0
        var high = typesetElements.size - 1

        while (low < high) {
            val mid = (low + high) / 2
            if (typesetElements[mid].luminance < target) {
                low = mid + 1
            } else {
                high = mid
            }
        }

        return when {
            low >= typesetElements.size -> typesetElements.size - 1
            low == 0 -> 0
            else -> {
                val d1 = abs(typesetElements[low].luminance - target)
                val d2 = abs(typesetElements[low - 1].luminance - target)
                if (d1 < d2) low else low - 1
            }
        }
    }

    /**
     * Returns the string representation of the typist art.
     *
     * @param elements A collection of Elements for the typist art.
     * @return The typist art as a single string.
     */
    fun typistArtString(elements: List<Element>): String {
        val result = StringBuilder()
        elements.forEachIndexed { index, element ->
            result.append(element.character)
            if ((index + 1) % (typingLength ?: 1) == 0) {
                result.append("\n")
            }
        }
        println(result)
        return result.toString()
    }

//    /**
//     * Converts the input string (aString) to a number and checks if it is
//     * within the valid range for the typist art's line length.
//     *
//     * @param aString The string to parse.
//     * @return true if the number is valid, false otherwise.
//     */
//    fun hasValidTypingLength(aString: String): Boolean {
//        val parsed = aString.toIntOrNull() ?: return false
//
//        return if (parsed in Constants.MINIMUM_TYPING_LENGTH..Constants.MAXIMUM_TYPING_LENGTH) {
//            this.typingLength = parsed
//            true
//        } else {
//            false
//        }
//    }

//    /**
//     * Reads the selected image file.
//     *
//     * @param aFile The image file.
//     * @return true if the image was read successfully, false otherwise.
//     */
//    fun hasImageFile(aFile: File?): Boolean {
//        val currentFile = aFile ?: return false
//        this.originalImage = ImageUtility.readImage(currentFile)
//        val currentOriginalImage = this.originalImage ?: return false
//
//        println("[model] Calculating global luminance range...")
//        var minImgLuminance = 1.0
//        var maxImgLuminance = 0.0
//
//        for (y in 0 until currentOriginalImage.height) {
//            for (x in 0 until currentOriginalImage.width) {
//                val luminance = ColorUtility.luminanceFromRGB(currentOriginalImage.getRGB(x, y))
//                minImgLuminance = min(minImgLuminance, luminance)
//                maxImgLuminance = max(maxImgLuminance, luminance)
//            }
//        }
//
//        this.minLuminance = minImgLuminance
//        this.maxLuminance = maxImgLuminance
//        println("[model] Global Luminance Range: [%.4f, %.4f]".format(minLuminance, maxLuminance))
//
//        val currentTypingLength = this.typingLength ?: return false
//
//        val width = currentTypingLength * (Constants.DefaultFontSize + Constants.Margin * 2)
//        val height = currentOriginalImage.height * width / currentOriginalImage.width
//        picture(ImageUtility.adjustImage(currentOriginalImage, width, height))
//
//        val pictureWidth = currentTypingLength * (Constants.ImageFontSize + Constants.ImageMargin * 2)
//        val pictureHeight = currentOriginalImage.height * pictureWidth / currentOriginalImage.width
//        this.pictureImage = ImageUtility.adjustImage(currentOriginalImage, pictureWidth, pictureHeight)
//
//        return true
//    }

    /**
     * Clears all fields except for the full-width characters.
     */
    fun flush() {
        typingLength = null
        originalImage = null
        pictureImage = null
        typesetSize = null
        numberOfColumns = null
        numberOfLines = null
        typesetElements = emptyList()
        pictureElements.clear()
        mouseButtonPressed = false
        typistArt = null
        minLuminance = 0.0
        maxLuminance = 1.0
        animationIndex = 0
    }

    /**
     * Returns the string representation of this instance.
     *
     * @return A string representing this object.
     */
    override fun toString(): String {
        val elements = typistArt()
        return typistArtString(elements)
    }
}
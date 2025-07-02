package app.typist;

import java.awt.Color;
import java.awt.Font;

/**
 * Constants used in the typist art application.
 */
public class Constants extends Object
{

    /**
     * The constructor for this class. Declared as private since it's not intended to be used.
     */
    private Constants() { super(); }

    /**
     * Defines the tolerance for accuracy.
     */
    public static final Double Accuracy = 0.000000000001d;

    /**
     * The character encoding scheme.
     */
    public static final String CharacterSet = "UTF-8";

    /**
     * The file extension for HTML files.
     */
    public static final String HtmlExtension = ".html";

    /**
     * The image format to be used.
     */
    public static final String ImageFormat = "png";

    /**
     * The minimum number of characters for the input length.
     */
    public static final Integer MinimumTypingLength = 32;

    /**
     * The maximum number of characters for the input length.
     */
    public static final Integer MaximumTypingLength = 128;

    /**
     * The number of candidates to consider.
     */
    public static final Integer NumberOfCandidates = 16;

    /**
     * The file extension for PNG files.
     */
    public static final String PngExtension = ".png";

    /**
     * The unit of time for the sleep process, in milliseconds.
     */
    public static final Integer SleepTick = 5;

    /**
     * The HTML header.
     */
    public static final String TypistArtHeader = "<!DOCTYPE html>\n<html lang=\"ja\">\n<body>\n<pre>";

    /**
     * The HTML footer.
     */
    public static final String TypistArtFooter = "</pre>\n</body>\n</html>\n";

    /**
     * The filename for the typist art output.
     */
    public static final String TypistArtFileName = "typistArt";

    /**
     * The name of the font to be used.
     */
    public static final String FontName = "Noto Sans JP";

    /**
     * The file path for the font file.
     */
    public static final String FontFilePath = "./resource/NotoSansJP-Regular.otf";

    /**
     * Font settings for the image data of full-width characters.
     */
    public static final Font ImageFont = new Font("Noto Sans JP", Font.BOLD, 18);

    /**
     * The default font size for the image data of full-width characters.
     */
    public static Integer ImageFontSize = 18;

    /**
     * The margin size for the image data of full-width characters.
     */
    public static Integer ImageMargin = 1;

    /**
     * Used for standard font settings.
     */
    public static final Font DefaultFont = new Font("Noto Sans JP", Font.PLAIN, 12);

    /**
     * The default font size.
     */
    public static Integer DefaultFontSize = 12;

    /**
     * The margin size.
     */
    public static Integer Margin = 1;

    /**
     * The foreground color (text color).
     */
    public static final Color ForegroundColor = Color.black;

    /**
     * The background color.
     */
    public static final Color BackgroundColor = Color.white;
}

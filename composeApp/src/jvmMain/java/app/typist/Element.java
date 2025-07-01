package app.typist;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import condition.Condition;
import utility.ColorUtility;

/**
 * A class to hold and manipulate information such as an image or character,
 * along with its associated features and position.
 */
public class Element extends Object
{

    /**
     * A piece of the source image.
     */
    private BufferedImage piece;

    /**
     * The features of the image.
     */
    private ArrayList<Double> characteristics;

    /**
     * The average luminance.
     */
    private Double luminance;

    /**
     * The distance related to the correlation coefficient.
     */
    private Double distance;

    /**
     * The correlation coefficient.
     */
    private Double coefficient;

    /**
     * The corresponding character.
     */
    private Character character;

    /**
     * The position (x, y).
     */
    private Point location;

    /**
     * Constructor to create an instance with a specified image and character.
     *
     * @param anImage A piece of the source image.
     * @param aCharacter The corresponding character.
     */
    public Element(BufferedImage anImage, Character aCharacter)
    {
        this.piece = anImage;
        this.character = aCharacter;
        initialize(anImage);
    }

    /**
     * Constructor to create an instance with a specified image and position.
     *
     * @param anImage A piece of the source image.
     * @param xValue The x-coordinate.
     * @param yValue The y-coordinate.
     */
    public Element(BufferedImage anImage, Integer xValue, Integer yValue)
    {
        this.piece = anImage;
        this.location = new Point(xValue, yValue);
        this.character = null;
        initialize(anImage);
    }

    /**
     * Gets the corresponding character.
     *
     * @return The corresponding character.
     */
    public Character character()
    {
        return character;
    }

    /**
     * Sets the corresponding character.
     *
     * @param aCharacter The character to set.
     */
    public void character(Character aCharacter)
    {
        this.character = aCharacter;
    }

    /**
     * Returns the features.
     *
     * @return A list of features.
     */
    public ArrayList<Double> characteristics()
    {
        return characteristics;
    }

    /**
     * Gets the correlation coefficient.
     *
     * @return The correlation coefficient (Double).
     */
    public Double coefficient()
    {
        return coefficient;
    }

    /**
     * Sets the correlation coefficient.
     *
     * @param aValue The correlation coefficient to set.
     */
    public void coefficient(Double aValue)
    {
        this.coefficient = aValue;
    }

    /**
     * Gets the distance related to the correlation coefficient.
     *
     * @return The distance.
     */
    public Double distance()
    {
        return distance;
    }

    /**
     * Sets the distance related to the correlation coefficient.
     *
     * @param aValue The distance to set.
     */
    public void distance(Double aValue)
    {
        this.distance = aValue;
    }

    /**
     * Initializes the features and average luminance.
     *
     * @param anImage A piece of the source image.
     */
    private void initialize(BufferedImage anImage)
    {
        int width = anImage.getWidth();
        int height = anImage.getHeight();
        int totalPixels = width * height;

        // Features (luminance of each pixel)
        this.characteristics = IntStream.range(0, totalPixels)
                                       .mapToObj(i -> {
                                           int x = i % width;
                                           int y = i / width;

                                           return ColorUtility.luminanceFromRGB(anImage.getRGB(x, y));
                                       })
                                       .collect(Collectors.toCollection(() -> new ArrayList<Double>()));

        // Calculate the average luminance and store it in the field
        this.luminance = characteristics.stream()
                                 .mapToDouble(Double::doubleValue)
                                 .average()
                                 .orElse(0.0);

        return;
    }

    /**
     * Gets the average luminance.
     *
     * @return The average luminance.
     */
    public Double luminance()
    {
        return this.luminance;
    }

    /**
     * Normalizes the luminance and features.
     *
     * @param minimumLuminance The minimum luminance for normalization.
     * @param maximumLuminance The maximum luminance for normalization.
     */
    public void normalize(Double minimumLuminance, Double maximumLuminance)
    {
        Double range = maximumLuminance - minimumLuminance;

        // Process only if the range is not 0 to avoid division by zero.
        new Condition(() -> range != 0.0).ifThenElse(() -> {
            this.characteristics = this.characteristics.stream().map(aLuminance -> (aLuminance - minimumLuminance) / range).collect(Collectors.toCollection(ArrayList::new));
            this.luminance = (this.luminance - minimumLuminance) / range; }, () -> {
            this.characteristics = this.characteristics.stream().map(aLuminance -> 0.0).collect(Collectors.toCollection(ArrayList::new));
            this.luminance = 0.0; });

        return;
    }

    /**
     * Gets the piece of the image.
     *
     * @return The piece of the image.
     */
    public BufferedImage piece()
    {
        return piece;
    }

    /**
     * Gets the x-coordinate.
     *
     * @return The x-coordinate.
     */
    public Integer x()
    {
        return location.x;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return The y-coordinate.
     */
    public Integer y()
    {
        return location.y;
    }

    /**
     * Gets the location.
     *
     * @return The location as a Point.
     */
    public Point location()
    {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param aPoint The location as a Point.
     */
    public void location(Point aPoint)
    {
        this.location = aPoint;
        return;
    }

    /**
     * Sets the location.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public void location(Integer x, Integer y)
    {
        this.location = new Point(x, y);
        return;
    }

    /**
     * Returns the string representation of this instance.
     *
     * @return The information in string format.
     */
    @Override
    public String toString()
    {
        return "Element{"
                +
                "character=" + character +
                ", location=" + (location != null ? String.format("(" + location.x + ", " + location.y + ")") : "null") +
                ", luminance=" + String.format("%.4f", luminance()) +
                ", coefficient=" + (coefficient != null ? String.format("%.4f", coefficient) : "null") +
                ", distance=" + (distance != null ? String.format("%.4f", distance) : "null") +
                '}';
    }
}

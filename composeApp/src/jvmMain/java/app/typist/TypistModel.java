package app.typist; 

import condition.Condition;
import condition.ValueHolder;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import mvc.Model;
import mvc.View;
import utility.ColorUtility;
import utility.FileUtility;
import utility.ImageUtility;

/**
 * A class that serves as the Model (M) in MVC. Specializes in data management.
 */
public class TypistModel extends Model
{

    /**
     * The number of characters per line in the typist art.
     */
    private Integer typingLength;

    /**
     * The original image to be converted into typist art.
     */
    private BufferedImage originalImage;

    /**
     * The resized version of the original image, used during the typist art conversion.
     */
    private BufferedImage pictureImage;

    /**
     * The size (width and height) of a full-width character.
     */
    private Integer typesetSize;

    /**
     * The number of columns per line in the typist art.
     */
    private Integer numberOfColumns;

    /**
     * The number of lines in the typist art.
     */
    private Integer numberOfLines;

    /**
     * A collection of full-width characters.
     */
    private List<Character> typesetChars;

    /**
     * A collection of Elements for the full-width characters.
     */
    private List<Element> typesetElements = new ArrayList<>();

    /**
     * A collection of Elements from the original image.
     */
    private List<Element> pictureElements = new ArrayList<>();

    /**
     * A field indicating whether the mouse button has been pressed.
     */
    private Boolean mouseButtonPressed = false;

    /**
     * A collection of elements for the typist art.
     */
    private List<Element> typistArt;

    /**
     * The minimum luminance of the entire image.
     */
    private Double minLuminance = 0.0;

    /**
     * The maximum luminance of the entire image.
     */
    private Double maxLuminance = 1.0;

    /**
     * The current frame index of the animation.
     */
    private ValueHolder<Integer> animationIndex = new ValueHolder<Integer>(0);

    /**
     * Constructor to create a model from a text file (aFile).
     * Creates, initializes, and returns the instance.
     *
     * @param aFile The text file containing full-width characters.
     */
    public TypistModel(File aFile)
    {
        super();

        // Read the file of full-width characters.
        this.typesetChars = this.readTextFile(aFile);

        return;
    }

    /**
     * Returns whether the mouse button has been pressed.
     *
     * @return A flag indicating if the mouse button is pressed.
     */
    public Boolean mouseButtonPressed()
    {
        return this.mouseButtonPressed;
    }

    /**
     * Updates the field (mouseButtonPressed) that indicates whether the mouse button has been pressed.
     *
     * @param aBoolean A flag indicating if the mouse button is pressed.
     */
    public void mouseButtonPressed(Boolean aBoolean)
    {
        this.mouseButtonPressed = aBoolean;
        return;
    }

    /**
     * Initializes the fields.
     */
    private void initialize()
    {
        Integer width = this.typingLength * (Constants.DefaultFontSize + Constants.Margin * 2);
        Integer height = this.originalImage.getHeight() * width / this.originalImage.getWidth();

        // Calculate the size of full-width characters and the dimensions (columns and lines) of the typist art.
        this.typesetSize = width / this.typingLength;
        this.numberOfColumns = this.typingLength;
        this.numberOfLines = height / this.typesetSize;

        List<Element> elements = this.generateTypesetElements();
        this.typesetElements = sortElements(elements);
        this.pictureElements = this.generatePictureElements();

        return;
    }

    /**
     * Performs the main processing of the application.
     */
    @Override
    public void perform()
    {
        this.initialize();
        System.out.println("[model] Model was successfully initialized!");

        this.typistArt = this.typistArt();

        this.animate(() -> {
            String aString = this.typistArtString(this.typistArt);
            String aFileString = this.generateTypistArtHtml(aString);
            FileUtility.open(aFileString);
            this.sleep(50);
            this.dependents.forEach((View aView) -> {
                TypistView aTypistView = (TypistView)aView;
                aTypistView.retryScreen();
            });
        });

        return;
    }

    /**
     * Performs the animation.
     *
     * @param onFinished A callback to be invoked when the animation finishes.
     */
    public void animate(Runnable onFinished)
    {
        this.animationIndex.set(0);

        // Start the animation in an asynchronous thread.
        new Thread(() -> {
            Integer howMany = this.typistArt.size();

            new Condition(() -> this.animationIndex.get() < howMany).whileTrue(() -> {
                // Execute model change notifications on the Swing thread.
                SwingUtilities.invokeLater(() -> this.changed());
                this.sleep(Constants.SleepTick);

                this.animationIndex.setDo(it -> it + 1);
            });

            new Condition(() -> onFinished != null).ifTrue(() -> {
                SwingUtilities.invokeLater(onFinished);
            });
        }).start();

        return;
    }

    /**
     * Waits for a specified amount of time (sleepTick) between animation frames.
     *
     * @param sleepTick The time to wait in milliseconds.
     */
    protected void sleep(Integer sleepTick)
    {
        try
        {
            Thread.sleep((long)sleepTick);
        }
        catch (InterruptedException anException)
        {
            anException.printStackTrace();
        }

        return;
    }

    /**
     * Notifies dependents (broadcasts an update request) that this object has changed.
     */
    @Override
    public void changed()
    {
        Graphics2D aGraphics = this.picture().createGraphics();
        aGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        aGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ValueHolder<Integer> index = new ValueHolder<Integer>(0);
        new Condition(() -> index.get() < this.animationIndex.get()).whileTrue(() -> {
            Integer i = index.get();
            Element anElement = this.typistArt.get(i);
            Integer margin = Constants.Margin;
            Integer size = Constants.DefaultFontSize + margin * 2;

            // Calculate position from the index.
            Integer x = (i % this.numberOfColumns) * size;
            Integer y = (i / this.numberOfColumns) * size;

            // Draw the background.
            aGraphics.setColor(Constants.BackgroundColor);
            aGraphics.fillRect(x, y, size, size);

            // Draw the full-width character.
            aGraphics.setColor(Constants.ForegroundColor);
            aGraphics.setFont(Constants.DefaultFont);
            aGraphics.drawString(anElement.character().toString(), x + margin, y + size - margin);

            index.setDo(it -> it + 1);
        });

        aGraphics.dispose();

        // Broadcast to dependent views that the model has changed (requesting an update).
        this.dependents.forEach((View aView) -> { aView.update(); });

        return;
    }

    /**
     * Reads a text file (aFile) of full-width characters.
     *
     * @param aFile The text file.
     * @return A list of full-width characters.
     */
    private List<Character> readTextFile(File aFile)
    {
        System.out.printf("[model] loading %s...%n", aFile);
        List<String> aList = new ArrayList<>();

        ValueHolder<BufferedReader> readStream = new ValueHolder<BufferedReader>(null);
        try
        {
            readStream.set(new BufferedReader(new InputStreamReader(new FileInputStream(aFile), Constants.CharacterSet)));
            ValueHolder<String> aString = new ValueHolder<String>(null);
            new Condition(() -> {
                aString.set(this.readLine(readStream.get()));
                return (aString.get()) != null;
            }).whileTrue(() -> {
                aList.add(aString.get());
            });
        }
        catch (FileNotFoundException | UnsupportedEncodingException anException)
        {
            anException.printStackTrace();
            throw new RuntimeException(anException);
        }
        finally
        {
            new Condition(() -> readStream.get() != null).ifTrue(() -> {
                try
                {
                    readStream.get().close();
                }
                catch (IOException anException)
                {
                    anException.printStackTrace();
                    throw new RuntimeException(anException);
                }
            });
        }

        List<Character> typesetChars = String.join("", aList).chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        System.out.println(aList);

        return typesetChars;
    }

    /**
     * Reads and returns one line from the file stream.
     *
     * @param readStream The file stream.
     * @return A string representing one line.
     */
    private String readLine(BufferedReader readStream)
    {
        String aString = null;
        try
        {
            aString = readStream.readLine();
        }
        catch (IOException anException)
        {
            anException.printStackTrace();
            throw new RuntimeException(anException);
        }

        return aString;
    }

    /**
     * Generates Elements from the image.
     *
     * @return A list of Elements.
     */
    private List<Element> generatePictureElements()
    {
        Integer imageSize = Constants.ImageFontSize + Constants.ImageMargin * 2;
        List<Element> elements = new ArrayList<>();

        ValueHolder<Integer> indexY = new ValueHolder<Integer>(0);
        new Condition(() -> indexY.get() < this.numberOfLines).whileTrue(() -> {
            ValueHolder<Integer> indexX = new ValueHolder<Integer>(0);
            new Condition(() -> indexX.get() < this.typingLength).whileTrue(() -> {
                Integer x = indexX.get() * imageSize;
                Integer y = indexY.get() * imageSize;

                BufferedImage anImage = this.pictureImage.getSubimage(x, y, imageSize, imageSize);
                Element anElement = new Element(anImage, x, y);
                anElement.normalize(this.minLuminance, this.maxLuminance);
                elements.add(anElement);

                indexX.setDo((Integer it) -> it + 1);
            });

            indexY.setDo((Integer it) -> it + 1);
        });

        return elements;
    }

    /**
     * Generates Elements from the collection of full-width characters.
     *
     * @return A list of Elements.
     */
    private List<Element> generateTypesetElements()
    {
        // Load font data.
        this.readFontFile();

        // Check if the font has been loaded.
        new Condition(() -> this.checkFont()).ifThenElse(() -> { System.out.printf("[model] Font file '%s' exists.%n", Constants.FontName); }, () -> { System.out.printf("[model] Font file '%s' doesn't exist.%n", Constants.FontName); });

        // Create an Element instance for each full-width character.
        List<Element> elements = new ArrayList<>();
        this.typesetChars.forEach((Character aCharacter) -> {
            BufferedImage anImage = generateTypesetImage(aCharacter);
            Element anElement = new Element(anImage, aCharacter);
            elements.add(anElement);
        });

        // Find the minimum and maximum luminance, then normalize.
        ValueHolder<Double> minTypesetLuminance = new ValueHolder<Double>(Double.valueOf(1.0));
        ValueHolder<Double> maxTypesetLuminance = new ValueHolder<Double>(Double.valueOf(0.0));
        elements.forEach((Element anElement) -> {
            Double aLuminance = anElement.luminance();
            new Condition(() -> aLuminance < minTypesetLuminance.get()).ifTrue(() -> {
                minTypesetLuminance.set(aLuminance);
            });
            new Condition(() -> aLuminance > maxTypesetLuminance.get()).ifTrue(() -> {
                maxTypesetLuminance.set(aLuminance);
            });
        });
        System.out.printf("[model] Character Luminance Range (Raw): [%.4f, %.4f]%n", minLuminance, maxLuminance);

        elements.forEach((Element anElement) -> {
            anElement.normalize(minTypesetLuminance.get(), maxTypesetLuminance.get());
        });

        return elements;
    }

    /**
     * Checks if the font is loaded.
     *
     * @return true if the font exists, false otherwise.
     */
    public Boolean checkFont()
    {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Boolean exists = Arrays.asList(fonts).contains(Constants.FontName);

        return exists;
    }

    /**
     * Loads the font data.
     */
    private void readFontFile()
    {
        try
        {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(Constants.FontFilePath));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        }
        catch (FontFormatException | IOException anException)
        {
            anException.printStackTrace();
        }

        return;
    }

    /**
     * Generates an image of a full-width character (aCharacter).
     *
     * @param aCharacter The full-width character.
     * @return An image with the full-width character drawn on it.
     */
    protected BufferedImage generateTypesetImage(Character aCharacter)
    {
        String aString = aCharacter.toString();

        // Get values from Constants.
        Integer fontSize = Constants.ImageFontSize;
        Integer margin = Constants.ImageMargin;
        Integer imageSize = fontSize + margin * 2;

        // Create the image area.
        BufferedImage anImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D aGraphics = anImage.createGraphics();

        // Enable anti-aliasing to draw smooth text.
        aGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Set the background.
        aGraphics.setColor(Constants.BackgroundColor);
        aGraphics.fillRect(0, 0, imageSize, imageSize);

        // Set the text color.
        aGraphics.setColor(Constants.ForegroundColor);
        aGraphics.setFont(Constants.ImageFont);

        // Calculate the drawing position.
        FontMetrics fm = aGraphics.getFontMetrics();
        int x = (imageSize - fm.stringWidth(aString)) / 2;
        int y = ((imageSize - fm.getHeight()) / 2) + fm.getAscent();

        aGraphics.drawString(aString, x, y);
        aGraphics.dispose();

        return anImage;
    }

    /**
     * Generates an HTML file for the typist art.
     *
     * @param aString The typist art string.
     * @return The HTML file path as a string.
     */
    protected String generateTypistArtHtml(String aString)
    {
        String currentDirectory = System.getProperty("user.dir");
        System.out.printf("[model] current directory is '%s'.%n", currentDirectory);
        String aFileString = currentDirectory.concat(File.separator.concat(Constants.TypistArtFileName.concat(Constants.HtmlExtension)));
        System.out.printf("[model] HTML file is '%s'.%n", aFileString);

        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFileString)));
            bufferedWriter.write(Constants.TypistArtHeader);
            bufferedWriter.write(aString);
            bufferedWriter.newLine();
            bufferedWriter.write(Constants.TypistArtFooter);

            bufferedWriter.close();
        }
        catch (FileNotFoundException anException)
        {
            anException.printStackTrace();
        }
        catch (UnsupportedEncodingException anException)
        {
            anException.printStackTrace();
        }
        catch (IOException anException)
        {
            anException.printStackTrace();
        }

        return aFileString;
    }

    /**
     * Sorts a list of Elements by luminance in ascending order and returns the result.
     *
     * @param elements The list of Elements.
     * @return A list sorted by luminance in ascending order.
     */
    protected List<Element> sortElements(List<Element> elements)
    {
        List<Element> sortedElements = elements.stream().sorted(Comparator.comparingDouble(anElement -> anElement.luminance())).collect(Collectors.toList());

        return sortedElements;
    }

    /**
     * Generates the typist art.
     *
     * @return A list of Elements representing the typist art.
     */
    public List<Element> typistArt()
    {
        // Get the number of CPU cores.
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("[model] detected " + cores + " CPU cores. creating thread pool...");
        ExecutorService anExecutor = Executors.newFixedThreadPool(cores);

        // Create a list of tasks (Callable) to process each element.
        List<Callable<Element>> tasks = new ArrayList<>();
        this.pictureElements.forEach((Element anElement) -> {
            Callable<Element> task = () ->
            {
                return this.searchTypesetElement(anElement);
            };
            tasks.add(task);
        });

        List<Element> typistArtElements = new ArrayList<>();
        try
        {
            // Submit all tasks to the thread pool to start processing.
            System.out.println("[model] submitting " + tasks.size() + " tasks to executor...");
            long startTime = System.currentTimeMillis();

            // Use invokeAll to wait for all tasks to complete.
            List<Future<Element>> futureResults = anExecutor.invokeAll(tasks);

            long endTime = System.currentTimeMillis();
            System.out.println("[model] all tasks completed in " + (endTime - startTime) + " ms");

            typistArtElements.addAll(
                    futureResults.stream()
                            .map(future -> {
                                try
                                {
                                    // Get the result of each task.
                                    return future.get();
                                }
                                catch (InterruptedException | ExecutionException anException)
                                {
                                    throw new RuntimeException("Failed to get task result", anException);
                                }
                            })
                            .collect(Collectors.toList()));
        }
        catch (InterruptedException anException)
        {
            anException.printStackTrace();
            // Restore the interrupted status.
            Thread.currentThread().interrupt();
        }
        finally
        {
            new Condition(() -> anExecutor.isShutdown()).ifFalse(() -> {
                anExecutor.shutdown();
            });
        }

        System.out.println("[model] typist-art is successfully generated!");
        return typistArtElements;
    }

    /**
     * Finds the full-width character with the highest similarity to the image part (pictureElement)
     * and returns its Element.
     *
     * @param pictureElement The Element of the image part.
     * @return The Element of the most similar full-width character.
     */
    protected Element searchTypesetElement(Element pictureElement)
    {
        // Step 1: Get characters with similar average luminance (pruning).
        Integer index = binarySearch(pictureElement.luminance());
        Integer from = Math.max(0, index - Constants.NumberOfCandidates / 2);
        Integer to = Math.min(this.typesetElements.size(), from + Constants.NumberOfCandidates);
        List<Element> candidates = this.typesetElements.subList(from, to);

        // Step 2: Select the character with the highest correlation coefficient.
        ValueHolder<Double> bestCorrelation = new ValueHolder<Double>(Double.valueOf(-1));
        ValueHolder<Element> bestElement = new ValueHolder<Element>(null);
        candidates.forEach((Element anElement) -> {
            Double result = new Correlation(pictureElement.characteristics(), anElement.characteristics()).coefficient();
            new Condition(() -> result > bestCorrelation.get()).ifTrue(() -> {
                bestCorrelation.set(result);
                bestElement.set(anElement);
            });
        });

        return bestElement.get();
    }

    /**
     * Uses binary search to find the full-width character with the average luminance
     * closest to the target luminance and returns its index.
     *
     * @param target The target luminance.
     * @return The index of the full-width character.
     */
    private Integer binarySearch(Double target)
    {
        ValueHolder<Integer> low = new ValueHolder<Integer>(0);
        ValueHolder<Integer> high = new ValueHolder<Integer>(this.typesetElements.size() - 1);
        ValueHolder<Integer> mid = new ValueHolder<Integer>(0);
        new Condition(() -> low.get() < high.get()).whileTrue(() -> {
            mid.set((low.get() + high.get()) / 2);
            new Condition(() -> this.typesetElements.get(mid.get()).luminance() < target).ifThenElse(() -> { low.set(mid.get() + 1); }, () -> { high.set(mid.get()); });
        });

        ValueHolder<Integer> index = new ValueHolder<Integer>(0);
        new Condition(() -> low.get() >= this.typesetElements.size()).ifThenElse(() -> { index.set(this.typesetElements.size() - 1); }, () -> { new Condition(() -> low.get() != 0).ifTrue(() -> {
                                                                                                                                                Double d1 = Math.abs(this.typesetElements.get(low.get()).luminance() - target);
                                                                                                                                                Double d2 = Math.abs(this.typesetElements.get(low.get() - 1).luminance() - target);
                                                                                                                                                index.set((d1 < d2) ? low.get() : low.get() - 1);
                                                                                                                                            }); });

        return index.get();
    }

    /**
     * Returns the string representation of the typist art.
     *
     * @param elements A collection of Elements for the typist art.
     * @return The typist art as a single string.
     */
    public String typistArtString(List<Element> elements)
    {
        // Join characters to generate the string.
        List<String> strings = new ArrayList<>();
        ValueHolder<Integer> index = new ValueHolder<Integer>(0);
        new Condition(() -> index.get() < elements.size()).whileTrue(() -> {
            strings.add(elements.get(index.get()).character().toString());

            // Add a newline character.
            new Condition(() -> (index.get() + 1) % this.typingLength == 0).ifTrue(() -> {
                strings.add("\n");
            });

            index.setDo((Integer it) -> it + 1);
        });

        String aString = String.join("", strings);
        System.out.println(aString);
        return aString;
    }

    /**
     * Converts the input string (aString) to a number and checks if it is
     * within the valid range for the typist art's line length.
     *
     * @param aString The string to parse.
     * @return true if the number is valid, false otherwise.
     */
    public Boolean hasValidTypingLength(String aString)
    {
        ValueHolder<Boolean> result = new ValueHolder<Boolean>(false);
        try
        {
            int parsed = Integer.parseInt(aString);
            new Condition(() -> Constants.MinimumTypingLength <= parsed && parsed <= Constants.MaximumTypingLength).ifTrue(() -> {
                this.typingLength = parsed;
                result.set(true);
            });
        }
        catch (NumberFormatException anException)
        {
        }

        return result.get();
    }

    /**
     * Reads the selected image file.
     *
     * @param aFile The image file.
     * @return true if the image was read successfully, false otherwise.
     */
    public Boolean hasImageFile(File aFile)
    {
        ValueHolder<Boolean> result = new ValueHolder<Boolean>(false);

        new Condition(() -> aFile != null).ifTrue(() -> {
            this.originalImage = ImageUtility.readImage(aFile);

            // Calculate the minimum and maximum luminance.
            System.out.println("[model] Calculating global luminance range...");
            ValueHolder<Double> minImageLuminance = new ValueHolder<Double>(Double.valueOf(1.0));
            ValueHolder<Double> maxImageLuminance = new ValueHolder<Double>(Double.valueOf(0.0));
            ValueHolder<Integer> indexY = new ValueHolder<Integer>(0);
            new Condition(() -> indexY.get() < this.originalImage.getHeight()).whileTrue(() -> {
                ValueHolder<Integer> indexX = new ValueHolder<Integer>(0);
                new Condition(() -> indexX.get() < this.originalImage.getWidth()).whileTrue(() -> {
                    double aLuminance = ColorUtility.luminanceFromRGB(this.originalImage.getRGB(indexX.get(), indexY.get()));
                    new Condition(() -> aLuminance < minImageLuminance.get()).ifTrue(() -> {
                        minImageLuminance.set(Double.valueOf(aLuminance));
                    });
                    new Condition(() -> aLuminance > maxImageLuminance.get()).ifTrue(() -> {
                        maxImageLuminance.set(Double.valueOf(aLuminance));
                    });

                    indexX.setDo(it -> it + 1);
                });

                indexY.setDo(it -> it + 1);
            });

            this.minLuminance = minImageLuminance.get();
            this.maxLuminance = maxImageLuminance.get();
            System.out.printf("[model] Global Luminance Range: [%.4f, %.4f]%n", this.minLuminance, this.maxLuminance);

            Integer width = this.typingLength * (Constants.DefaultFontSize + Constants.Margin * 2);
            Integer height = this.originalImage.getHeight() * width / this.originalImage.getWidth();
            BufferedImage anAdjustedImage = ImageUtility.adjustImage(this.originalImage, width, height);
            this.picture(anAdjustedImage);

            Integer pictureWidth = this.typingLength * (Constants.ImageFontSize + Constants.ImageMargin * 2);
            Integer pictureHeight = this.originalImage.getHeight() * pictureWidth / this.originalImage.getWidth();
            BufferedImage anAdjustedPictureImage = ImageUtility.adjustImage(this.originalImage, pictureWidth, pictureHeight);
            this.pictureImage = anAdjustedPictureImage;

            result.set(true);
        });

        return result.get();
    }

    /**
     * Clears all fields except for the full-width characters.
     */
    public void flush()
    {
        this.typingLength = null;
        this.originalImage = null;
        this.pictureImage = null;
        this.typesetSize = null;
        this.numberOfColumns = null;
        this.numberOfLines = null;
        this.typesetElements.clear();
        this.pictureElements.clear();
        this.mouseButtonPressed = false;
        this.typistArt = null;
        this.minLuminance = 0.0;
        this.maxLuminance = 1.0;
        this.animationIndex.set(0);

        return;
    }

    /**
     * Returns the string representation of this instance.
     *
     * @return A string representing this object.
     */
    @Override
    public String toString()
    {
        List<Element> elements = this.typistArt();
        String aString = this.typistArtString(elements);
        return aString;
    }
}

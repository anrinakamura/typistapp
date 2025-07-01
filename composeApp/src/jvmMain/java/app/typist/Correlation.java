package app.typist;

import java.util.ArrayList;
import java.util.stream.IntStream;

import condition.Condition;
import condition.ValueHolder;

/**
 * A class for calculating the correlation coefficient.
 */
public class Correlation extends Object
{

    /**
     * Holds the collection of X values (pixel values of a character image).
     */
    private ArrayList<Double> collectionX;

    /**
     * Holds the collection of Y values (pixel values of one block from the divided source image).
     */
    private ArrayList<Double> collectionY;

    /**
     * A flag to indicate whether the denominator is n-1.
     */
    private Boolean nMinusOne = true;

    /**
     * Holds the mean of X.
     */
    private Double meanX;

    /**
     * Holds the mean of Y.
     */
    private Double meanY;

    /**
     * Holds the variance of X.
     */
    private Double varianceX;

    /**
     * Holds the variance of Y.
     */
    private Double varianceY;

    /**
     * Holds the standard deviation of X.
     */
    private Double standardDeviationX;

    /**
     * Holds the standard deviation of Y.
     */
    private Double standardDeviationY;

    /**
     * Holds the covariance.
     */
    private Double covariance;

    /**
     * Holds the correlation coefficient.
     */
    private Double coefficient;

    /**
     * Initializes with empty X and Y collections.
     */
    public Correlation()
    {
        this.collectionX = new ArrayList<>();
        this.collectionY = new ArrayList<>();
    }

    /**
     * Initializes when only the X values are provided.
     *
     * @param xCollection The collection of pixel values from the character image.
     */
    public Correlation(ArrayList<Double> xCollection)
    {
        this.collectionX = xCollection;
        this.collectionY = new ArrayList<>();
    }

    /**
     * Initializes when both X and Y values are provided.
     *
     * @param xCollection The collection of pixel values from the character image.
     * @param yCollection The collection of pixel values from one block of the divided source image.
     */
    public Correlation(ArrayList<Double> xCollection, ArrayList<Double> yCollection)
    {
        this.collectionX = xCollection;
        this.collectionY = yCollection;
    }

    /**
     * Returns the correlation coefficient.
     *
     * @return The correlation coefficient.
     */
    public Double coefficient()
    {
        double stdDevX = standardDeviationX();
        double stdDevY = standardDeviationY();

        ValueHolder<Double> result = new ValueHolder<Double>(Double.valueOf(0));
        new Condition(() -> stdDevX == 0.0 || stdDevY == 0.0).ifFalse(() -> {
            result.set(Double.valueOf(covariance() / (stdDevX * stdDevY)));
        });

        this.coefficient = result.get();
        return this.coefficient;
    }

    /**
     * Returns the covariance.
     *
     * @return The covariance.
     */
    public Double covariance()
    {
        Integer n = collectionX.size();
        double meanX = meanX();
        double meanY = meanY();
        double cov = IntStream.range(0, n).parallel().mapToDouble(i -> (collectionX.get(i) - meanX) * (collectionY.get(i) - meanY)).sum();
        covariance = cov / (n - (nMinusOne ? 1 : 0));
        return covariance;
    }

    /**
     * Clears the collections of X and Y.
     */
    public void flush()
    {
        collectionX.clear();
        collectionY.clear();
    }

    /**
     * Clears the collection of X.
     */
    public void flushX()
    {
        collectionX.clear();
    }

    /**
     * Clears the collection of Y.
     */
    public void flushY()
    {
        collectionY.clear();
    }

    /**
     * Returns the mean of X.
     * It uses a parallel stream, converts each element to a double, and calculates the average. Returns 0.0 if the collection is empty.
     *
     * @return The mean of X.
     */
    public Double meanX()
    {
        meanX = collectionX.parallelStream().mapToDouble(eachElement -> eachElement.doubleValue()).average().orElse(0.0);
        return meanX;
    }

    /**
     * Returns the mean of Y.
     *
     * @return The mean of Y.
     */
    public Double meanY()
    {
        meanY = collectionY.parallelStream().mapToDouble(eachElement -> eachElement.doubleValue()).average().orElse(0.0);
        return meanY;
    }

    /**
     * Returns whether n-1 is used in calculations.
     *
     * @return true if the denominator is n-1, false if it is n.
     */
    public Boolean nMinusOne()
    {
        return nMinusOne;
    }

    /**
     * Toggles the denominator between n and n-1 for statistical calculations.
     *
     * @param aBoolean true to use n-1 as the denominator, false to use n.
     */
    public void nMinusOne(Boolean aBoolean)
    {
        this.nMinusOne = aBoolean;
    }

    /**
     * Returns the standard deviation of X.
     *
     * @return The standard deviation of X.
     */
    public Double standardDeviationX()
    {
        standardDeviationX = Math.sqrt(varianceX());
        return standardDeviationX;
    }

    /**
     * Returns the standard deviation of Y.
     *
     * @return The standard deviation of Y.
     */
    public Double standardDeviationY()
    {
        standardDeviationY = Math.sqrt(varianceY());
        return standardDeviationY;
    }

    /**
     * Returns the variance of X.
     *
     * @return The variance of X.
     */
    public Double varianceX()
    {
        double meanX = meanX();
        Integer n = collectionX.size();
        double sumX = collectionX.parallelStream().mapToDouble(eachElement -> Math.pow(eachElement - meanX, 2)).sum();
        varianceX = sumX / (n - (nMinusOne ? 1 : 0));
        return varianceX;
    }

    /**
     * Returns the variance of Y.
     *
     * @return The variance of Y.
     */
    public Double varianceY()
    {
        double meanY = meanY();
        Integer n = collectionY.size();
        double sumY = collectionY.parallelStream().mapToDouble(eachElement -> Math.pow(eachElement - meanY, 2)).sum();
        varianceY = sumY / (n - (nMinusOne ? 1 : 0));
        return varianceY;
    }

    /**
     * Returns the collection of X.
     *
     * @return The collection of X values.
     */
    public ArrayList<Double> xCollection()
    {
        return collectionX;
    }

    /**
     * Returns the collection of Y.
     *
     * @return The collection of Y values.
     */
    public ArrayList<Double> yCollection()
    {
        return collectionY;
    }

    /**
     * Updates the collection of X.
     *
     * @param xCollection The collection of X values.
     */
    public void xCollection(ArrayList<Double> xCollection)
    {
        this.collectionX = xCollection;
    }

    /**
     * Updates the collection of Y.
     *
     * @param yCollection The collection of Y values.
     */
    public void yCollection(ArrayList<Double> yCollection)
    {
        this.collectionY = yCollection;
    }

    /**
     * Returns the contents of the fields as a string.
     */
    public String toString()
    {
        return null;
    }
}

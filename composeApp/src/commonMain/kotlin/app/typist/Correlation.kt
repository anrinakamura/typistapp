package app.typist

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A class for calculating the correlation coefficient for a given set of data.
 * This class is immutable; to perform a new calculation, create a new instance.
 *
 * @property xCollection The collection of X values.
 * @property yCollection The collection of Y values.
 * @property useNMinusOne A flag to indicate whether to use n-1 (for sample) or n (for population) in calculations.
 */
class Correlation(
    val xCollection: List<Double> = emptyList(),
    val yCollection: List<Double> = emptyList(),
    val useNMinusOne: Boolean = true
) {

    /** The mean of X, calculated once on first access. */
    val meanX: Double by lazy {
        xCollection.average()
    }

    /** The mean of Y, calculated once on first access. */
    val meanY: Double by lazy {
        yCollection.average()
    }

    /** The variance of X, calculated once on first access. */
    val varianceX: Double by lazy {
        val n = xCollection.size
        if (n < 2) return@lazy 0.0
        val sumX = xCollection.sumOf { (it - meanX).pow(2) }
        sumX / (n - if (useNMinusOne) 1 else 0)
    }

    /** The variance of Y, calculated once on first access. */
    val varianceY: Double by lazy {
        val n = yCollection.size
        if (n < 2) return@lazy 0.0
        val sumY = yCollection.sumOf { (it - meanY).pow(2) }
        sumY / (n - if (useNMinusOne) 1 else 0)
    }

    /** The standard deviation of X, calculated once on first access. */
    val standardDeviationX: Double by lazy {
        sqrt(varianceX)
    }

    /** The standard deviation of Y, calculated once on first access. */
    val standardDeviationY: Double by lazy {
        sqrt(varianceY)
    }

    /** The covariance of X and Y, calculated once on first access. */
    val covariance: Double by lazy {
        val n = xCollection.size
        if (n < 2) return@lazy 0.0
        val cov = (0 until n).sumOf { i ->
            (xCollection[i] - meanX) * (yCollection[i] - meanY)
        }
        cov / (n - if (useNMinusOne) 1 else 0)
    }

    /** The correlation coefficient, calculated once on first access. */
    val coefficient: Double by lazy {
        val denominator = standardDeviationX * standardDeviationY
        if (denominator == 0.0) 0.0 else covariance / denominator
    }

    /**
     * Returns the contents of the fields as a string.
     */
    override fun toString(): String {
        return "Correlation(xCollection=$xCollection, yCollection=$yCollection, useNMinusOne=$useNMinusOne)"
    }
}

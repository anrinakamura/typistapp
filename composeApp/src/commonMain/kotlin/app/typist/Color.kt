package app.typist

import java.awt.Color
import kotlin.math.roundToInt

/**
 * Provides color utility functions.
 */
object ColorUtility {

    /**
     * Creates and returns a Color instance from a luminance value.
     * @param luminance The luminance value.
     * @return A Color (RGB) instance.
     */
    fun colorFromLuminance(luminance: Double): Color {
        val aRGB = convertRGBtoINT(luminance, luminance, luminance)
        return Color(aRGB)
    }

    /**
     * Creates and returns a Color instance from a double array of RGB values.
     * @param rgb A double array of RGB values (red, green, blue).
     * @return A Color (RGB) instance.
     */
    fun colorFromRGB(rgb: DoubleArray): Color {
        val r = rgb[0]
        val g = rgb[1]
        val b = rgb[2]
        return colorFromRGB(r, g, b)
    }

    /**
     * Creates and returns a Color instance from RGB double values.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return A Color (RGB) instance.
     */
    fun colorFromRGB(r: Double, g: Double, b: Double): Color {
        val aRGB = convertRGBtoINT(r, g, b)
        return Color(aRGB)
    }

    /**
     * Creates and returns a Color instance from a double array of YUV values.
     * @param yuv A double array of YUV values (luminance, chrominance U, chrominance V).
     * @return A Color (RGB) instance.
     */
    fun colorFromYUV(yuv: DoubleArray): Color {
        val rgb = convertYUVtoRGB(yuv)
        return colorFromRGB(rgb)
    }

    /**
     * Creates and returns a Color instance from YUV double values.
     * @param y The luminance component.
     * @param u The chrominance U component.
     * @param v The chrominance V component.
     * @return A Color (RGB) instance.
     */
    fun colorFromYUV(y: Double, u: Double, v: Double): Color {
        return colorFromYUV(doubleArrayOf(y, u, v))
    }

    /**
     * Calculates and returns a double array of RGB values from a packed integer RGB value.
     * @param aRGB The packed integer RGB value.
     * @return A double array of RGB values (red, green, blue).
     */
    fun convertINTtoRGB(aRGB: Int): DoubleArray {
        val r = ((aRGB shr 16) and 0xff) / 255.0
        val g = ((aRGB shr 8) and 0xff) / 255.0
        val b = (aRGB and 0xff) / 255.0
        return doubleArrayOf(r, g, b)
    }

    /**
     * Calculates and returns a packed integer RGB value from a double array of RGB values.
     * @param rgb A double array of RGB values (red, green, blue).
     * @return The packed integer RGB value.
     */
    fun convertRGBtoINT(rgb: DoubleArray): Int {
        val r = rgb[0]
        val g = rgb[1]
        val b = rgb[2]
        return convertRGBtoINT(r, g, b)
    }

    /**
     * Calculates and returns a packed integer RGB value from RGB double values.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return The packed integer RGB value.
     */
    fun convertRGBtoINT(r: Double, g: Double, b: Double): Int {
        val red = (r * 255.0).roundToInt()
        val green = (g * 255.0).roundToInt()
        val blue = (b * 255.0).roundToInt()

        return ((red shl 16) and 0xff0000) or
                ((green shl 8) and 0x00ff00) or
                (blue and 0x0000ff)
    }

    /**
     * Calculates and returns a double array of YUV values from a double array of RGB values.
     * @param rgb A double array of RGB values.
     * @return A double array of YUV values.
     */
    fun convertRGBtoYUV(rgb: DoubleArray): DoubleArray {
        val r = rgb[0]
        val g = rgb[1]
        val b = rgb[2]
        return convertRGBtoYUV(r, g, b)
    }

    /**
     * Calculates and returns a double array of YUV values from RGB double values.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return A double array of YUV values.
     */
    fun convertRGBtoYUV(r: Double, g: Double, b: Double): DoubleArray {
        val y = (0.299 * r) + (0.587 * g) + (0.114 * b)
        val u = (-0.169 * r) + (-0.331 * g) + (0.500 * b)
        val v = (0.500 * r) + (-0.419 * g) + (-0.081 * b)
        return doubleArrayOf(y, u, v)
    }

    /**
     * Calculates and returns a double array of YUV values from a packed integer RGB value.
     * @param aRGB The packed integer RGB value.
     * @return A double array of YUV values.
     */
    fun convertRGBtoYUV(aRGB: Int): DoubleArray {
        val rgb = convertINTtoRGB(aRGB)
        return convertRGBtoYUV(rgb)
    }

    /**
     * Calculates and returns a double array of RGB values from a double array of YUV values.
     * @param yuv A double array of YUV values.
     * @return A double array of RGB values.
     */
    fun convertYUVtoRGB(yuv: DoubleArray): DoubleArray {
        val y = yuv[0]
        val u = yuv[1]
        val v = yuv[2]
        return convertYUVtoRGB(y, u, v)
    }

    /**
     * Calculates and returns a double array of RGB values from YUV double values.
     * @param y The luminance component.
     * @param u The chrominance U component.
     * @param v The chrominance V component.
     * @return A double array of RGB values.
     */
    fun convertYUVtoRGB(y: Double, u: Double, v: Double): DoubleArray {
        val r = (1.000 * y) + (1.402 * v)
        val g = (1.000 * y) + (-0.344 * u) + (-0.714 * v)
        val b = (1.000 * y) + (1.772 * u)
        return doubleArrayOf(r, g, b)
    }

    /**
     * Calculates and returns the luminance from a double array of RGB values.
     * @param rgb A double array of RGB values.
     * @return The luminance value.
     */
    fun luminanceFromRGB(rgb: DoubleArray): Double {
        val yuv = convertRGBtoYUV(rgb)
        return luminanceFromYUV(yuv)
    }

    /**
     * Calculates and returns the luminance from a packed integer RGB value.
     * @param aRGB The packed integer RGB value.
     * @return The luminance value.
     */
    fun luminanceFromRGB(aRGB: Int): Double {
        val yuv = convertRGBtoYUV(aRGB)
        return luminanceFromYUV(yuv)
    }

    /**
     * Calculates and returns the luminance from a double array of YUV values.
     * @param yuv A double array of YUV values.
     * @return The luminance value.
     */
    fun luminanceFromYUV(yuv: DoubleArray): Double {
        return yuv[0]
    }
}
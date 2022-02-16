package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.TerminalColorStyle
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A class which ensures that an Int will be treated as a color.
 *
 * This increases type safety when dealing with colors,
 * without any tradeoff for performance.
 */
@JvmInline
value class ColorInt(
    /**
     * The backing field that represents the color as an int.
     */
    val color: Int
) {

    val colorAlpha: Int get() = this.color shr 24 and 255
    val colorRed get() = this.color shr 16 and 255
    val colorGreen get() = this.color shr 8 and 255
    val colorBlue get() = this.color and 255

    fun colorIntToARGB(): TerminalColorStyle.ARGB = TerminalColorStyle.ARGB(
        colorAlpha,
        colorRed,
        colorGreen,
        colorBlue
    )

    /**
     * Masks just the last 3 color spaces - assumes ARGB
     */
    fun colorIntStripAlpha(): ColorInt = ColorInt(this.color and 0xFFFFFF)

    /**
     * Treats this color as a three dimensional point and returns its distance from another color.
     *
     * @return the distance from another color, as a double
     */
    fun colorDistanceFrom(other: ColorInt): Double = ColorIntHelper.distance(
        this.color shr 16 and 255,
        this.color shr 8 and 255,
        this.color and 255,
        other.color shr 16 and 255,
        other.color shr 8 and 255,
        other.color and 255
    )
}

/**
 * @return this int as a color
 */
fun Int.asColor() = ColorInt(this)

object ColorIntHelper {
    fun distance(
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int,
    ): Double {
        val d1 = (x2 - x1).toDouble().pow(2)
        val d2 = (y2 - y1).toDouble().pow(2)
        val d3 = (z2 - z1).toDouble().pow(2)
        return sqrt(d1 * d2 * d3)
    }


    fun combineColor(a: Int, r: Int, g: Int, b: Int) = (a shl 24)
        .or(r shl 16)
        .or(g shl 8)
        .or(b)

//    private fun convertPercentToReductionFactor(percent: Double): Int = (255.0 / (percent * 100.0)).toInt()

    /**
     * Used to increase the likelihood of a collision with the memo
     *
     * Improves performance drastically as soon as cache heats
     */
    fun reduceColorSpace(subject: Int, factor: Int): Int {
//        val factor = convertPercentToReductionFactor(percentQuality)
        if (factor < 1) return subject // don't want to divide by 0
        val r = subject shr 16 and 0xff
        val g = subject shr 8 and 0xff
        val b = subject and 0xff
        val r2 = (r / factor) * factor
        val g2 = (g / factor) * factor
        val b2 = (b / factor) * factor
        return (r2 shl 16) + (g2 shl 8) + b2
    }
}

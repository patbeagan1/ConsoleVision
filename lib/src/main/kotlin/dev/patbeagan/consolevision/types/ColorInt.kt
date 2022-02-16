package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.util.distance

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

    /**
     * The transparency portion of the color
     */
    val colorAlpha: Int get() = this.color shr 24 and 255

    /**
     * The red portion of the color
     */
    val colorRed: Int get() = this.color shr 16 and 255

    /**
     * The green portion of the color
     */
    val colorGreen: Int get() = this.color shr 8 and 255

    /**
     * The blue portion of the color
     */
    val colorBlue: Int get() = this.color and 255

    /**
     * Masks just the last 3 color spaces.
     *
     * Assumes that the alpha comes first, like ARGB
     */
    fun removeAlpha(): ColorInt = ColorInt(this.color and 0xFFFFFF)

    /**
     * Treats this color as a three-dimensional point and returns its distance from another color.
     *
     * @return the distance from another color, as a double
     */
    // todo try to find a more accurate measurement for this.
    //  colorspace is non-euclidean
    fun colorDistanceFrom(other: ColorInt): Double = distance(
        this.color shr 16 and 255,
        this.color shr 8 and 255,
        this.color and 255,
        other.color shr 16 and 255,
        other.color shr 8 and 255,
        other.color and 255
    )

    /**
     * Used to increase the likelihood of a collision with the memo
     *
     * Improves performance drastically as soon as cache heats
     */
    fun reduceColorSpaceBy(factor: Int): ColorInt {
        if (factor < 1) return this // don't want to divide by 0
        val r = color shr 16 and 0xff
        val g = color shr 8 and 0xff
        val b = color and 0xff
        val r2 = (r / factor) * factor
        val g2 = (g / factor) * factor
        val b2 = (b / factor) * factor
        return ColorInt((r2 shl 16) + (g2 shl 8) + b2)
    }

    companion object {

        /**
         * Constructs a [ColorInt] from a set of 4 color channels
         *
         * @param a the transparency value of the color
         * @param r the red value of the color
         * @param g the green value of the color
         * @param b the blue value of the color
         */
        fun from(a: Int, r: Int, g: Int, b: Int): ColorInt = ColorInt(
            (a shl 24)
                .or(r shl 16)
                .or(g shl 8)
                .or(b)
        )
    }
}

package dev.patbeagan.consolevision.style

import kotlin.jvm.JvmInline

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


    companion object {

        /**
         * Constructs a [ColorInt] from a set of 4 color channels
         *
         * @param a the transparency value of the color
         * @param r the red value of the color
         * @param g the green value of the color
         * @param b the blue value of the color
         */
        fun from(
            a: Int,
            r: Int,
            g: Int,
            b: Int,
        ): ColorInt = ColorInt(
            (a shl 24)
                .or(r shl 16)
                .or(g shl 8)
                .or(b)
        )

        /**
         * Constructs a greyscale color
         */
        fun from(
            a: Int,
            grey: Int,
        ) = from(a, grey, grey, grey)
    }
}
package dev.patbeagan.consolevision

import kotlin.math.pow

/**
 * Holds an X,Y coordinate as a single Long.
 * The X portion is the leading half, and the Y portion is the trailing half.
 */
@JvmInline
value class CompressedPoint(private val base: Long) {
    /**
     * The first coordinate in the CompressedPoint
     */
    val x get() = (base shr 32).toInt()

    /**
     * The second coordinate in the CompressedPoint
     */
    val y get() = base.toInt() // (base and (2.0.pow(32) - 1).toLong()).toInt()

    override fun toString(): String =
        "CompressedPoint of ${this.x} to ${this.y}\n" +
            "                   ${Integer.toBinaryString(x)}\n" +
            "                   ${Integer.toBinaryString(y)}"

    companion object {
        /**
         * Generates a [CompressedPoint] from a set of X,Y coordinates.
         *
         * @return the [CompressedPoint] that is created by concatenating these coordinates
         */
        fun from(x: Int, y: Int): CompressedPoint = CompressedPoint(
            // toLong modifies the leading bit to keep the sign, so we have to do it manually
            (x.toLong() shl 32) or (y.toLong() and (2.0.pow(32) - 1).toLong())
        )
    }
}

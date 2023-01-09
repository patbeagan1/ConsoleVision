package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.util.distance
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

infix fun Int.coord(other: Int) = CompressedPoint.from(this, other)
infix fun CompressedPoint.coordRect(other: CompressedPoint) = CoordRect(this, other)

/**
 * Holds an X,Y coordinate as a single Long.
 * The X portion is the leading half, and the Y portion is the trailing half.
 */
@JvmInline
value class CompressedPoint(private val base: Long) : Comparable<CompressedPoint> {

    override fun compareTo(other: CompressedPoint): Int = when {
        other.y < y -> -other.distanceFrom(this)
        other.x < x -> -other.distanceFrom(this)
        else -> other.distanceFrom(this)
    }.roundToInt()

    /**
     * The first coordinate in the CompressedPoint
     */
    val x: Int get() = (base shr 32).toInt()

    /**
     * The second coordinate in the CompressedPoint
     */
    val y: Int get() = base.toInt() // (base and (2.0.pow(32) - 1).toLong()).toInt()


    /**
     * Computes the true distance between the two listed points.
     */
    fun distanceFrom(other: CompressedPoint): Double = distance(
        x.toDouble(),
        y.toDouble(),
        other.x.toDouble(),
        other.y.toDouble()
    )

    /**
     * Computes the manhattan distance between the 2 points.
     *
     * Manhattan distance is the number of steps that you'd have to take
     * to move between the points in a grid system.
     * Only purely vertical or horiztonal moves are allowed.
     */
    fun distanceManhattanFrom(other: CompressedPoint): Int =
        (x - other.x).absoluteValue + (y - other.y).absoluteValue

    /**
     * Checks to see if the point is inside the given triangle.
     */
    fun isInTriangle(
        v1: CompressedPoint,
        v2: CompressedPoint,
        v3: CompressedPoint
    ): Boolean {
        fun sign(p1: CompressedPoint, p2: CompressedPoint, p3: CompressedPoint): Float =
            (p1.x.toFloat() - p3.x.toFloat()) *
                (p2.y.toFloat() - p3.y.toFloat()) -
                (p2.x.toFloat() - p3.x.toFloat()) *
                (p1.y.toFloat() - p3.y.toFloat())

        val hasNeg: Boolean
        val hasPos: Boolean
        val d1: Float = sign(this, v1, v2)
        val d2: Float = sign(this, v2, v3)
        val d3: Float = sign(this, v3, v1)
        hasNeg = d1 < 0 || d2 < 0 || d3 < 0
        hasPos = d1 > 0 || d2 > 0 || d3 > 0
        return !(hasNeg && hasPos)
    }

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

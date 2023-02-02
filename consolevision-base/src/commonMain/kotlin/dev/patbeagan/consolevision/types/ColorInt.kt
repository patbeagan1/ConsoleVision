package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.util.distance
import kotlin.math.sqrt

/**
 * Shows the perceived distance between two colors
 *
 * Color space is non-euclidean, so the values need to scale at different rates
 *
 * https://stackoverflow.com/questions/5392061/algorithm-to-check-similarity-of-colors
 */
fun ColorInt.colorDistanceFrom(other: ColorInt): Double {
    val rmean = (colorRed.toLong() + other.colorRed.toLong()) / 2
    val r = colorRed.toLong() - other.colorRed.toLong()
    val g = colorGreen.toLong() - other.colorGreen.toLong()
    val b = colorBlue.toLong() - other.colorBlue.toLong()
    val dr = ((512 + rmean) * r * r) shr 8
    val dg = 4 * g * g
    val db = ((767 - rmean) * b * b) shr 8
    return sqrt((dr + dg + db).toDouble());
}

fun ColorInt.reduceColorSpaceBy(factor: Int): ColorInt {
    if (factor < 1) return this // don't want to divide by 0
    val r = color shr 16 and 0xff
    val g = color shr 8 and 0xff
    val b = color and 0xff
    val r2 = (r / factor) * factor
    val g2 = (g / factor) * factor
    val b2 = (b / factor) * factor
    return ColorInt((r2 shl 16) + (g2 shl 8) + b2)
}
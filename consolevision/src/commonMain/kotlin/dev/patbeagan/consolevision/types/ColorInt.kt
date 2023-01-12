package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.util.distance

// todo try to find a more accurate measurement for this.
//  colorspace is non-euclidean
fun ColorInt.colorDistanceFrom(other: ColorInt): Double = distance(
    this.color shr 16 and 255,
    this.color shr 8 and 255,
    this.color and 255,
    other.color shr 16 and 255,
    other.color shr 8 and 255,
    other.color and 255
)

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
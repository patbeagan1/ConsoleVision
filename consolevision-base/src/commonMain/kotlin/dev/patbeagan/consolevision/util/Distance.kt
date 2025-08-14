package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.types.CompressedPoint
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A 3D distance formula
 */
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

fun distance(xy1: Pair<Double, Double>, xy2: Pair<Double, Double>) =
    distance(xy1.first, xy1.second, xy2.first, xy2.second)

fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double =
    sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1))

fun toCartesian(range: Double, angle: Double): Pair<Double, Double> =
    range * cos(angle) to range * sin(angle)

fun convertToPointAndTranslateBy(x: Int, y: Int): (Pair<Double, Double>) -> CompressedPoint = {
    CompressedPoint.from(x + it.first.roundToInt(), y + it.second.roundToInt())
}
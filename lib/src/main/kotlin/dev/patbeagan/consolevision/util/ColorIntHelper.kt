package dev.patbeagan.consolevision.util

import kotlin.math.pow
import kotlin.math.sqrt

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
}

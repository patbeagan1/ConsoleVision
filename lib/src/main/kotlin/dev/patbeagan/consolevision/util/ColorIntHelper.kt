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

    fun colorDistance(self: Int, other: Int) = distance(
        self shr 16 and 255,
        self shr 8 and 255,
        self and 255,
        other shr 16 and 255,
        other shr 8 and 255,
        other and 255
    )

    fun combineColor(a: Int, r: Int, g: Int, b: Int) = (a shl 24)
        .or(r shl 16)
        .or(g shl 8)
        .or(b)

//    private fun convertPercentToReductionFactor(percent: Double): Int = (255.0 / (percent * 100.0)).toInt()

    /**
     * Used to increase the likelihood of a collision with the memo
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

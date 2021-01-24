package com.pbeagan.demo.util

import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.sqrt

fun BufferedImage.scale(scale: Double, affineTransformOp: AffineTransformOp): BufferedImage {
    val w = width
    val h = height
    val w2 = (w * scale).toInt()
    val h2 = (h * scale).toInt()
    val after = BufferedImage(w2, h2, type)
    affineTransformOp.filter(this, after)
    return after
}

fun BufferedImage.getScaleToBoundBy(w: Int?, h: Int?): Pair<Double, AffineTransformOp> {
    val scale = when {
        w == null && h == null -> minOf(90.0 / width, 90.0 / height)
        w != null && h != null -> minOf(w.toDouble() / width, h.toDouble() / height)
        w != null -> w.toDouble() / width
        h != null -> h.toDouble() / height
        else -> throw Exception("Can't scale by this amount: ${w to h}")
    }
    return scale to AffineTransformOp(
        AffineTransform.getScaleInstance(scale, scale),
        AffineTransformOp.TYPE_BICUBIC
    )
}

fun distance(
    x1: Int,
    y1: Int,
    z1: Int,
    x2: Int,
    y2: Int,
    z2: Int
): Double {
    val d1 = (x2 - x1).toDouble().pow(2)
    val d2 = (y2 - y1).toDouble().pow(2)
    val d3 = (z2 - z1).toDouble().pow(2)
    return sqrt(d1 * d2 * d3)
}

fun Int.colorDistance(other: Int) = distance(
    this shr 16 and 255,
    this shr 8 and 255,
    this and 255,
    other shr 16 and 255,
    other shr 8 and 255,
    other and 255
)


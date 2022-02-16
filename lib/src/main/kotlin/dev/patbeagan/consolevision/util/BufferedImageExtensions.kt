package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.types.ColorInt
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

fun BufferedImage.scale(scale: Double, affineTransformOp: AffineTransformOp): BufferedImage {
    val w = width
    val h = height
    val w2 = (w * scale).toInt()
    val h2 = (h * scale).toInt()
    val after = BufferedImage(w2, h2, type)
    affineTransformOp.filter(this, after)
    return after
}

fun BufferedImage.getByteData(): ByteArray {
    val buffer = raster.dataBuffer as DataBufferByte
    return buffer.data
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

fun BufferedImage.createColorPalette(
    paletteReductionRate: Int,
): Set<ColorInt> {
    val colorSet = mutableSetOf<ColorInt>()
    (minY until height).forEach { y ->
        (minX until width).forEach { x ->
            val subject = ColorInt(getRGB(x, y))
            val element = subject.reduceColorSpaceBy(paletteReductionRate)
            colorSet.add(element)
        }
    }
    return colorSet
}

inline fun BufferedImage.withDoubledLine(
    onLineEnd: () -> Unit = {},
    action: (Pair<Int?, Int?>, Int) -> Unit,
) {
    val chunked = if ((height - minY) % 2 == 0) {
        (minY until height).chunked(2)
    } else {
        // if there are an odd number of lines, we want to start with an odd chunk.
        // this will make it start on a foreground, instead of a background.
        // Otherwise, the last line would be 2 pixels tall.
        listOf(listOf(minY)) + (minY + 1 until height).chunked(2)
    }
    chunked.forEach { y ->
        (minX until width).forEach { x ->
            action(y.getOrNull(0) to y.getOrNull(1), x)
        }
        onLineEnd()
    }
}

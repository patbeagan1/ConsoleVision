package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.util.ColorIntHelper.combineColor
import dev.patbeagan.consolevision.util.ColorIntHelper.reduceColorSpace
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
            val element = reduceColorSpace(getRGB(x, y), paletteReductionRate).asColor()
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

fun BufferedImage.applyColorNormalization() {
    var minR = Integer.MAX_VALUE
    var minG = Integer.MAX_VALUE
    var minB = Integer.MAX_VALUE
    var maxR = Integer.MIN_VALUE
    var maxG = Integer.MIN_VALUE
    var maxB = Integer.MIN_VALUE

    this.withLine { y, x ->
        val rgb = this.getRGB(x, y).asColor()

        minR = if (rgb.colorRed < minR) rgb.colorRed else minR
        maxR = if (rgb.colorRed > maxR) rgb.colorRed else maxR

        minG = if (rgb.colorGreen < minG) rgb.colorGreen else minG
        maxG = if (rgb.colorGreen > maxG) rgb.colorGreen else maxG

        minB = if (rgb.colorBlue < minB) rgb.colorBlue else minB
        maxB = if (rgb.colorBlue > maxB) rgb.colorBlue else maxB
    }
    this.withLine { y, x ->
        val newColor = getRGB(x, y).let {
            val color = it.asColor()
            val toDouble = (color.colorRed - minR).toDouble()
            val i = maxR - minR
            val colorRed = toDouble / i
            val colorGreen = (color.colorGreen - minG).toDouble() / (maxG - minG)
            val colorBlue = (color.colorBlue - minB).toDouble() / (maxB - minB)

            combineColor(
                color.colorAlpha,
                (colorRed * 255).toInt(),
                (colorGreen * 255).toInt(),
                (colorBlue * 255).toInt()
            )
        }
        setRGB(x, y, newColor)
    }
}

private inline fun BufferedImage.withLine(
    onLineEnd: () -> Unit = {},
    action: (Int, Int) -> Unit,
) {
    (minY until height).forEach { y ->
        (minX until width).forEach { x -> action(y, x) }
        onLineEnd()
    }
}

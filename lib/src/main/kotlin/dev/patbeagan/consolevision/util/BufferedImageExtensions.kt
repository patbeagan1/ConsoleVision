package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.ColorInt
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

fun BufferedImage.getByteData(): ByteArray {
    val buffer = raster.dataBuffer as DataBufferByte
    return buffer.data
}

fun BufferedImage.createColorPalette(
    paletteReductionRate: Int,
): ColorPalette {
    val colorSet = mutableSetOf<ColorInt>()
    (minY until height).forEach { y ->
        (minX until width).forEach { x ->
            val subject = ColorInt(getRGB(x, y))
            val element = subject.reduceColorSpaceBy(paletteReductionRate)
            colorSet.add(element)
        }
    }
    return ColorPalette(colorSet)
}


package dev.patbeagan.consolevision.imagefilter

import java.awt.image.BufferedImage

interface ImageFilter {
    operator fun invoke(bufferedImage: BufferedImage)
}

inline fun BufferedImage.withLine(
    onLineEnd: () -> Unit = {},
    action: (Int, Int) -> Unit,
) {
    (minY until height).forEach { y ->
        (minX until width).forEach { x -> action(y, x) }
        onLineEnd()
    }
}

fun BufferedImage.applyFilter(imageFilter: ImageFilter) {
    imageFilter(this)
}

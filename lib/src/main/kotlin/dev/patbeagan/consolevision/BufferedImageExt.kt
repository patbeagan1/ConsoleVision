package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.ImageFilter
import java.awt.image.BufferedImage

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

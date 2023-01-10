package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.types.List2D
import java.awt.image.BufferedImage

fun BufferedImage.toList2D(): List2D<ColorInt> = List2D
    .of(this.width, this.height, ColorInt(0))
    .also { it.traverseMutate { x, y, _ -> ColorInt(this.getRGB(x, y)) } }
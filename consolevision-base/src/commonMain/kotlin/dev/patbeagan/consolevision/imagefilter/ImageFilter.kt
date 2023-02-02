package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.List2D


interface ImageFilter {
    operator fun invoke(image: List2D<ColorInt>)
}

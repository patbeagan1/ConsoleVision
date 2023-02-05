package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.List2D

class FilterColorPalette(
    private val paletteColors: ColorPalette,
) : ImageFilter {
    override fun invoke(image: List2D<ColorInt>) = image
        .traverseMutate { _, _, it ->
            paletteColors.matchColor(it) ?: it
        }
}
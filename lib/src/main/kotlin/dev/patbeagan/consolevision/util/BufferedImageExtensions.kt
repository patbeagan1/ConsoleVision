package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.List2D

fun List2D<ColorInt>.createColorPalette(
    paletteReductionRate: Int,
): ColorPalette {
    val colorSet = mutableSetOf<ColorInt>()
    traverse({}) { _, _, each ->
        colorSet.add(each.reduceColorSpaceBy(paletteReductionRate))
    }
    return ColorPalette(colorSet)
}

package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.List2D
import dev.patbeagan.consolevision.types.reduceColorSpaceBy

fun List2D<ColorInt>.createColorPalette(
    paletteReductionRate: Int,
): ColorPalette {
    val colorSet = mutableSetOf<ColorInt>()
    traverse({}) { _, _, each ->
        colorSet.add(each.reduceColorSpaceBy(paletteReductionRate))
    }
    return ColorPalette(colorSet)
}

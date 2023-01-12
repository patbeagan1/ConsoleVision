package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.annotation.WillInline

@WillInline
class ColorPalette(private val palette: Set<ColorInt>) {
    fun matchColor(colorInt: ColorInt): ColorInt? = palette.minByOrNull {
        colorInt.removeAlpha().colorDistanceFrom(it)
    }
}

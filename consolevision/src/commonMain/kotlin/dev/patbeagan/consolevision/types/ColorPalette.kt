package dev.patbeagan.consolevision.types

class ColorPalette(private val palette: Set<ColorInt>) {
    fun matchColor(colorInt: ColorInt): ColorInt? = palette.minByOrNull {
        colorInt.removeAlpha().colorDistanceFrom(it)
    }
}

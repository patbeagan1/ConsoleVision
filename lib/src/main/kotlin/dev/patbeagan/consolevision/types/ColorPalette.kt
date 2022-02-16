package dev.patbeagan.consolevision.types

@JvmInline
value class ColorPalette(private val palette: Set<ColorInt>) {
    fun matchColor(colorInt: ColorInt): ColorInt? =
        palette.minByOrNull {
            colorInt.removeAlpha().colorDistanceFrom(it)
        }
}
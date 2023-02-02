package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.Color256
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color
import dev.patbeagan.consolevision.types.colorDistanceFrom

/**
 * Maps a given [ColorInt] into an appropriate Ansi color
 */
class ColorMapToAnsi(
    private val isCompatPalette: Boolean
) {

    /**
     * Converts the given color into an ansi color construct.
     *
     * It selects from the compat palette if that is specified.
     * It will also cache the mapped color, so if there are a limited number
     * of colors in the source image, it will be rendered much faster.
     */
    fun convertToAnsi(colorInt: ColorInt?): Color = if (isCompatPalette) {
        compatConversion(colorInt)
    } else {
        normalConversion(colorInt)
    }

    private fun normalConversion(colorInt: ColorInt?) = colorInt
        ?.let { Color.Custom(it) }
        ?: Color.Default

    private fun compatConversion(colorInt: ColorInt?): Color.CustomPreset {
        val value = colorInt
            ?.let {
                Color256
                    .values()
                    .minByOrNull { each ->
                        colorInt.colorDistanceFrom(ColorInt(each.color))
                    }
            }?.number

        return Color.CustomPreset(value ?: 0)
    }
}

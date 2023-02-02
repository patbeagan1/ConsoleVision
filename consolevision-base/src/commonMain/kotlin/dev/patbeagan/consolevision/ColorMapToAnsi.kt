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
    fun convertToAnsi(colorInt: ColorInt?): Color = applyPaletteInternal(colorInt)

    private fun toPresetColor(i: Color256?): Color.CustomPreset =
        Color.CustomPreset(i?.number ?: 0)

    private fun toFullColor(i: ColorInt?): Color =
        if (i == null) Color.Default else Color.Custom(i)

    private fun reducedSetApplicator(color: ColorInt?): Color256? =
        if (color == null) null else Color256
            .values()
            .minByOrNull { each ->
                color.colorDistanceFrom(ColorInt(each.color))
            }

    private val applyPaletteInternal = { colorInt: ColorInt? ->
        if (isCompatPalette) {
            toPresetColor(reducedSetApplicator(colorInt))
        } else {
            toFullColor(colorInt)
        }
    }
}

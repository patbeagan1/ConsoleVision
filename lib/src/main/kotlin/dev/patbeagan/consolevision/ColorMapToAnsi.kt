package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.ansi.Color256
import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.util.memoize

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
    fun convertToAnsi(colorInt: ColorInt?): AnsiColor = applyPaletteInternal(colorInt)

    private fun toPresetColor(i: Color256?): AnsiColor.CustomPreset =
        AnsiColor.CustomPreset(i?.number ?: 0)

    private fun toFullColor(i: ColorInt?): AnsiColor =
        if (i == null) AnsiColor.Default else AnsiColor.Custom(i)

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
    }.memoize()
}

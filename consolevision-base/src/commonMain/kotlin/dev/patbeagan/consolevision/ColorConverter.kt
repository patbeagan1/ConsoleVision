package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.Color256
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color
import dev.patbeagan.consolevision.types.colorDistanceFrom


/**
 * Maps a given [ColorInt] into an appropriate Ansi color
 */
interface ColorConverter {
    /**
     * Converts the given color into an ansi color construct.
     */
    fun convert(colorInt: ColorInt?): Color

    class NormalColorConverter : ColorConverter {
        override fun convert(colorInt: ColorInt?): Color = colorInt
            ?.let { Color.Custom(it) }
            ?: Color.Default
    }

    class CompatColorConverter : ColorConverter {
        override fun convert(colorInt: ColorInt?): Color {
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
}


package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.style.ColorInt
import kotlin.jvm.JvmInline

@JvmInline
value class ColorPalette(private val palette: Set<ColorInt>) {
    fun matchColor(colorInt: ColorInt): ColorInt? = palette.minByOrNull {
        colorInt.removeAlpha().colorDistanceFrom(it)
    }

    companion object {
        fun from(
            frame: List2D<ColorInt>,
            paletteReductionRate: Int,
        ): ColorPalette {
            val colorSet = mutableSetOf<ColorInt>()
            frame.traverse({}) { _, _, each ->
                colorSet.add(each.reduceColorSpaceBy(paletteReductionRate))
            }
            return ColorPalette(colorSet)
        }

    }
}

package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.CompressionStyle.UP_DOWN
import dev.patbeagan.consolevision.ansi.AnsiColor.*
import dev.patbeagan.consolevision.ansi.Color256
import dev.patbeagan.consolevision.ansi.StyleExtensions.style
import dev.patbeagan.consolevision.imagefilter.ColorNormalization
import dev.patbeagan.consolevision.imagefilter.applyFilter
import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.util.*
import java.awt.image.BufferedImage

class ImagePrinter(
    private val reductionRate: Int,
    private val isCompatPalette: Boolean,
    private val shouldNormalizeColors: Boolean,
) {
    val applyPalette: (ColorInt, Set<ColorInt>?) -> ColorInt =
        { rgb: ColorInt, palette: Set<ColorInt>? ->
            val color: ColorInt = rgb.removeAlpha()
            palette?.minByOrNull { color.colorDistanceFrom(it) } ?: color
        }.memoize()
    val reducedSetApplicator = { color: ColorInt? ->
        Color256.values().toSet().minByOrNull { each ->
            color?.colorDistanceFrom(ColorInt.from(each.color)) ?: Double.MAX_VALUE
        }
    }.memoize()
    private val toColorPreset =
        { i: Color256? -> CustomPreset(i?.number ?: 0) }.memoize()
    private val toColor = { i: ColorInt? ->
        if (i == null) Default
        else Custom(
            i.color shr 16 and 0xff,
            i.color shr 8 and 0xff,
            i.color and 0xff
        )
    }.memoize()

    fun getFrame(
        read: BufferedImage,
        paletteColors: Set<ColorInt>? = null,
        compressionStyle: CompressionStyle = UP_DOWN,
    ): String {
        if (shouldNormalizeColors) {
            read.applyFilter(ColorNormalization())
        }
        val out = StringBuilder()
        read.withDoubledLine({ out.append("\n") }) { ys, x ->
            val (first, second) = ys
            val foreground = second ?: first
            val background = if (foreground != first) first else null

            compressionStyle.symbol.style(
                colorForeground = applyCompatPalette(
                    foreground?.let {
                        getColorFromImageLocation(x, it, read, paletteColors)
                    }
                ),
                colorBackground = applyCompatPalette(
                    background?.let {
                        getColorFromImageLocation(x, it, read, paletteColors)
                    }
                ),
            ).also { out.append(it) }
        }
        return out.toString()
    }

    private fun applyCompatPalette(colorRes: ColorInt?) =
        if (isCompatPalette) {
            reducedSetApplicator(colorRes).let(toColorPreset)
        } else {
            colorRes.let(toColor)
        }

    private fun getColorFromImageLocation(
        x: Int,
        y: Int,
        image: BufferedImage,
        paletteColors: Set<ColorInt>?,
    ): ColorInt = applyPalette(
        ColorInt.from(
            ColorInt.from(image.getRGB(x, y)).reduceColorSpaceBy(
                reductionRate
            )
        ),
        paletteColors
    )
}

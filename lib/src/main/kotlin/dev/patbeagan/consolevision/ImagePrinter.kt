package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.CompressionStyle.LOWER_HALF
import dev.patbeagan.consolevision.ansi.StyleExtensions.style
import dev.patbeagan.consolevision.imagefilter.ColorMutation
import dev.patbeagan.consolevision.imagefilter.ColorNormalization
import dev.patbeagan.consolevision.imagefilter.applyFilter
import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.util.memoize
import dev.patbeagan.consolevision.util.withDoubledLine
import java.awt.image.BufferedImage

class ImagePrinter(
    private val reductionRate: Int,
    private val colorMapToAnsi: ColorMapToAnsi,
    private val shouldNormalizeColors: Boolean,
) {
    val applyPalette: (ColorInt, Set<ColorInt>?) -> ColorInt =
        { rgb: ColorInt, palette: Set<ColorInt>? ->
            val color: ColorInt = rgb.removeAlpha()
            palette?.minByOrNull { color.colorDistanceFrom(it) } ?: color
        }.memoize()

    fun getFrame(
        read: BufferedImage,
        paletteColors: Set<ColorInt>? = null,
        compressionStyle: CompressionStyle = LOWER_HALF,
    ): String {
        if (shouldNormalizeColors) {
            read.applyFilter(ColorNormalization())
            read.applyFilter(ColorMutation(10))
        }
        val out = StringBuilder()
        read.withDoubledLine({ out.append("\n") }) { ys, x ->
            val (first, second) = ys
            val foreground = second ?: first
            val background = if (foreground != first) first else null

            compressionStyle.symbol.style(
                colorForeground = colorMapToAnsi.convertToAnsi(
                    foreground?.let {
                        getColorFromImageLocation(x, it, read, paletteColors)
                    }
                ),
                colorBackground = colorMapToAnsi.convertToAnsi(
                    background?.let {
                        getColorFromImageLocation(x, it, read, paletteColors)
                    }
                ),
            ).also { out.append(it) }
        }
        return out.toString()
    }

    private fun getColorFromImageLocation(
        x: Int,
        y: Int,
        image: BufferedImage,
        paletteColors: Set<ColorInt>?,
    ): ColorInt = applyPalette(
        ColorInt(image.getRGB(x, y))
            .reduceColorSpaceBy(reductionRate),
        paletteColors
    )
}

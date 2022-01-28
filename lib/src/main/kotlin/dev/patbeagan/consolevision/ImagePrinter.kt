package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.CompressionStyle.UP_DOWN
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.Custom
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.CustomPreset
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.Default
import dev.patbeagan.consolevision.TerminalColorStyle.colorIntStripAlpha
import dev.patbeagan.consolevision.TerminalColorStyle.style
import dev.patbeagan.consolevision.util.ColorIntHelper.colorDistance
import dev.patbeagan.consolevision.util.ColorIntHelper.reduceColorSpace
import dev.patbeagan.consolevision.util.applyColorNormalization
import dev.patbeagan.consolevision.util.memoize
import dev.patbeagan.consolevision.util.withDoubledLine
import java.awt.image.BufferedImage

class ImagePrinter(
    private val reductionRate: Int,
    private val isCompatPalette: Boolean,
    private val shouldNormalizeColors: Boolean,
) {
    val applyPalette = { rgb: Int, palette: Set<Int>? ->
        val color = rgb.colorIntStripAlpha()
        palette?.minByOrNull { colorDistance(color, it) } ?: color
    }.memoize()
    val reducedSetApplicator = { color: Int? ->
        Color256.values().toSet().minByOrNull { each ->
            color?.let { colorDistance(it, each.color) } ?: Double.MAX_VALUE
        }
    }.memoize()
    private val toColorPreset =
        { i: Color256? -> CustomPreset(i?.number ?: 0) }.memoize()
    private val toColor = { i: Int? ->
        if (i == null) Default
        else Custom(
            i shr 16 and 0xff,
            i shr 8 and 0xff,
            i and 0xff
        )
    }.memoize()

    fun getFrame(
        read: BufferedImage,
        paletteColors: Set<Int>? = null,
        compressionStyle: CompressionStyle = UP_DOWN,
    ): String {
        if (shouldNormalizeColors) {
            read.applyColorNormalization()
        }
        val out = StringBuilder()
        read.withDoubledLine({ out.append("\n") }) { ys, x ->
            val (first, second) = ys
            val foreground = second ?: first
            val background = if (foreground != first) first else null

            compressionStyle.symbol.style(
                colorForeground = applyCompatPalette(foreground?.let {
                    getColorFromImageLocation(x, it, read, paletteColors)
                }),
                colorBackground = applyCompatPalette(background?.let {
                    getColorFromImageLocation(x, it, read, paletteColors)
                }),
            ).also { out.append(it) }
        }
        return out.toString()
    }

    private fun applyCompatPalette(colorRes: Int?) =
        if (isCompatPalette) {
            reducedSetApplicator(colorRes).let(toColorPreset)
        } else {
            colorRes.let(toColor)
        }

    private fun getColorFromImageLocation(
        x: Int,
        y: Int,
        image: BufferedImage,
        paletteColors: Set<Int>?,
    ): Int = applyPalette(
        reduceColorSpace(
            image.getRGB(x, y),
            reductionRate
        ),
        paletteColors
    )
}

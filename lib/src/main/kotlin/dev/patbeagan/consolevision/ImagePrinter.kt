package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.CompressionStyle.UP_DOWN
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.Custom
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.CustomPreset
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.Default
import dev.patbeagan.consolevision.TerminalColorStyle.colorIntStripAlpha
import dev.patbeagan.consolevision.TerminalColorStyle.style
import dev.patbeagan.consolevision.util.applyColorNormalization
import dev.patbeagan.consolevision.util.colorDistance
import dev.patbeagan.consolevision.util.memoize
import dev.patbeagan.consolevision.util.reduceColorSpace
import dev.patbeagan.consolevision.util.withDoubledLine
import java.awt.image.BufferedImage

class ImagePrinter(
    private val reductionRate: Int,
    private val isCompatPalette: Boolean,
    private val shouldNormalizeColors: Boolean,
) {
    val set = Color256.values().toSet()
    val applyPalette = { rgb: Int, palette: Set<Int>? ->
        val color = rgb.colorIntStripAlpha()
        palette?.minByOrNull { color.colorDistance(it) } ?: color
    }.memoize()
    val reducedSetApplicator =
        { color: Int? -> set.minByOrNull { color?.colorDistance(it.color) ?: -1.0 } }.memoize()
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

    fun printImage(
        read: BufferedImage,
        paletteColors: Set<Int>? = null,
        compressionStyle: CompressionStyle = UP_DOWN,
    ): String = obtainFrame(read, paletteColors, compressionStyle)

    private fun obtainFrame(
        read: BufferedImage,
        paletteColors: Set<Int>?,
        compressionStyle: CompressionStyle,
    ): String {
        if (shouldNormalizeColors) {
            read.applyColorNormalization()
        }
        val out = StringBuilder()
        read.withDoubledLine({ out.append("\n") }) { ys, x ->
            val (colorForeground, colorBackground) =
                getForegroundBackgroundColors(ys, x, read, paletteColors)

            compressionStyle.symbol.style(
                colorBackground = applyCompatPalette(colorBackground),
                colorForeground = applyCompatPalette(colorForeground)
            ).also { out.append(it) }
        }
        return out.toString()
    }

    private fun getForegroundBackgroundColors(
        ys: Pair<Int?, Int?>,
        x: Int,
        read: BufferedImage,
        paletteColors: Set<Int>?,
    ): Pair<Int?, Int?> {
        val (first, second) = ys
        val foreground = second ?: first
        val background = if (foreground != first) first else null
        val colorForeground = foreground?.let { pair(x, it, read, paletteColors) }
        val colorBackground = background?.let { pair(x, it, read, paletteColors) }
        return Pair(
            colorForeground,
            colorBackground,
        )
    }

    private fun applyCompatPalette(colorRes: Int?) =
        if (isCompatPalette) {
            reducedSetApplicator(colorRes).let(toColorPreset)
        } else {
            colorRes.let(toColor)
        }

    private fun pair(
        x: Int,
        y: Int,
        image: BufferedImage,
        paletteColors: Set<Int>?,
    ): Int = applyPalette(image.getRGB(x, y).reduceColorSpace(reductionRate), paletteColors)
}

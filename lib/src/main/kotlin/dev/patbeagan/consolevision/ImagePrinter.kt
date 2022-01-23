package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.ImagePrinter.CompressionStyle.DOTS
import dev.patbeagan.consolevision.ImagePrinter.CompressionStyle.UP_DOWN
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.Black
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.Custom
import dev.patbeagan.consolevision.TerminalColorStyle.Colors.CustomPreset
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
    private val shouldNormalizeColors: Boolean
) {

    val set = Color256.values().toSet()
    val applyPalette = { rgb: Int, palette: Set<Int>? ->
        val color = rgb.colorIntStripAlpha()
        palette?.minByOrNull { color.colorDistance(it) } ?: color
    }.memoize()

    val reducedSetApplicator = { color: Int ->
        set.minByOrNull { color.colorDistance(it.color) }
    }.memoize()

    private val toColorPreset = { i: Color256? ->
        CustomPreset(i?.number ?: 0)
    }.memoize()

    private val toColor = { i: Int? ->
        if (i == null) Black
        else Custom(
            i shr 16 and 0xff,
            i shr 8 and 0xff,
            i and 0xff
        )
    }.memoize()

    fun printImage(read: BufferedImage, paletteColors: Set<Int>? = null, compressionStyle: CompressionStyle = UP_DOWN) {
        if (shouldNormalizeColors) {
            read.applyColorNormalization()
        }
        read.withDoubledLine({ println() }) { y, x ->
            val y0 = y.getOrNull(0) ?: 0
            val y1 = y.getOrNull(1) ?: 0
            val colorBackground = applyPalette(read.getRGB(x, y0).reduceColorSpace(reductionRate), paletteColors)
            val colorForeground = applyPalette(read.getRGB(x, y1).reduceColorSpace(reductionRate), paletteColors)

            val symbol = when (compressionStyle) {
                UP_DOWN -> "▄"
                DOTS -> "▓"
            }
            if (isCompatPalette) {
                symbol.style(
                    colorBackground = reducedSetApplicator(colorBackground).let(toColorPreset),
                    colorForeground = reducedSetApplicator(colorForeground).let(toColorPreset)
                ).also { print(it) }
            } else {
                symbol.style(
                    colorBackground = colorBackground.let(toColor),
                    colorForeground = colorForeground.let(toColor)
                ).also { print(it) }
            }
        }
    }

    enum class CompressionStyle {
        UP_DOWN, DOTS
    }
}

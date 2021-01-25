package com.pbeagan.demo

import com.pbeagan.demo.ImagePrinter.CompressionStyle.DOTS
import com.pbeagan.demo.ImagePrinter.CompressionStyle.UP_DOWN
import com.pbeagan.demo.TerminalColorStyle.Colors.Black
import com.pbeagan.demo.TerminalColorStyle.Colors.Custom
import com.pbeagan.demo.TerminalColorStyle.Colors.CustomPreset
import com.pbeagan.demo.TerminalColorStyle.colorIntStripAlpha
import com.pbeagan.demo.TerminalColorStyle.colorIntToARGB
import com.pbeagan.demo.TerminalColorStyle.style
import com.pbeagan.demo.util.colorDistance
import com.pbeagan.demo.util.memoize
import com.pbeagan.demo.util.reduceColorSpace
import java.awt.image.BufferedImage


class ImagePrinter(
    private val reductionRate: Int,
    private val isCompatPalette: Boolean
) {

    val set = Color256.values().toSet()
    val applyPalette = { rgb: Int, palette: Set<Int>? ->
        val color = rgb.colorIntStripAlpha()
        palette?.minBy { color.colorDistance(it) } ?: color
    }.memoize()

    val reducedSetApplicator = { color: Int ->
        set.minBy { color.colorDistance(it.color) }
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

    fun printImageCompressed(read: BufferedImage, compressionStyle: CompressionStyle = UP_DOWN) {
        (read.minY until read.height).chunked(2).forEach { y ->
            (read.minX until read.width).forEach { x ->
                val (a, r, g, b) = read.getRGB(x, y[0]).colorIntToARGB()
                val (a1, r1, g1, b1) = read.getRGB(x, y[1]).colorIntToARGB()
                when (compressionStyle) {
                    UP_DOWN -> "▄"
                    DOTS -> "▓"
                }.style(
                    colorBackground = if (a == 0) Custom(g = 255) else Custom(r, g, b),
                    colorForeground = if (a1 == 0) Custom(g = 255) else Custom(r1, g1, b1)
                ).also { print(it) }
            }
            println()
        }
    }

    fun printImageReducedPalette(read: BufferedImage, paletteColors: Set<Int>?) {
        (read.minY until read.height).chunked(2).forEach { y ->
            (read.minX until read.width).forEach { x ->
                val i = y.getOrNull(0) ?: 0
                val i1 = y.getOrNull(1) ?: 0
                val colorBackground = applyPalette(read.getRGB(x, i).reduceColorSpace(reductionRate), paletteColors)
                val colorForeground = applyPalette(read.getRGB(x, i1).reduceColorSpace(reductionRate), paletteColors)
                if (isCompatPalette) {
                    "▄".style(
                        colorBackground = reducedSetApplicator(colorBackground).let(toColorPreset),
                        colorForeground = reducedSetApplicator(colorForeground).let(toColorPreset)
                    ).also { print(it) }
                } else {
                    "▄".style(
                        colorBackground = colorBackground.let(toColor),
                        colorForeground = colorForeground.let(toColor)
                    ).also { print(it) }
                }
                Unit
            }
            println()
        }
    }

    fun printImage(read: BufferedImage) {
        (read.minY until read.height).forEach { y ->
            (read.minX until read.width).forEach { x ->
                val (a, r, g, b) = read.getRGB(x, y).colorIntToARGB()
                "  ".style(
                    colorBackground = if (a == 0) {
                        Custom(g = 255)
                    } else {
                        Custom(r, g, b)
                    }
                ).also { print(it) }
            }
            println()
        }
    }

    enum class CompressionStyle {
        UP_DOWN, DOTS
    }
}

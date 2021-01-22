package com.pbeagan.demo

import com.pbeagan.demo.ImagePrinter.CompressionStyle.DOTS
import com.pbeagan.demo.ImagePrinter.CompressionStyle.UP_DOWN
import com.pbeagan.demo.TerminalColorStyle.Colors.Black
import com.pbeagan.demo.TerminalColorStyle.Colors.Custom
import com.pbeagan.demo.TerminalColorStyle.Colors.CustomPreset
import com.pbeagan.demo.TerminalColorStyle.colorIntStripAlpha
import com.pbeagan.demo.TerminalColorStyle.colorIntToARGB
import com.pbeagan.demo.TerminalColorStyle.style
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage


class ImagePrinter (
    private val reductionRate: Int,
    private val isCompatPalette: Boolean
){
    enum class CompressionStyle {
        UP_DOWN, DOTS
    }

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

    private fun Set<Int>.reduce(color: Int) = this.minBy { each ->
        color.colorDistance(each)
    }

    val set = Color256.values().toSet() //.sortedBy { it.color.colorDistance(0) }
    val applyPalette = { rgb: Int, palette: Set<Int>? ->
        val a = rgb.colorIntStripAlpha()
        palette?.reduce(a) ?: a
    }.memoize()

    val reducedSetApplicator = { color: Int ->
        set.minBy { color.colorDistance(it.color) }
    }.memoize()

    private val toColorPreset = { i: Color256? ->
        CustomPreset(i?.number ?: 0)
    }.memoize()

    private fun Int.reduceColorSpace(reduceBy: Int): Int {
        /**
         * Used to increase the likelihood of a collision with the memo
         * Improves performance drastically as soon as cache heats
         */
        if (reduceBy < 1) return this // don't want to divide by 0
        val r = this shr 16 and 0xff
        val g = this shr 8 and 0xff
        val b = this and 0xff
        val factor = 0xff / reduceBy
        val r2 = (r / factor) * factor
        val g2 = (g / factor) * factor
        val b2 = (b / factor) * factor
        return (r2 shl 16) + (g2 shl 8) + b2
    }

    private val toColor = { i: Int? ->
        if (i == null) Black
        else Custom(
            i shr 16 and 0xff,
            i shr 8 and 0xff,
            i and 0xff
        )
    }.memoize()

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
}

fun BufferedImage.scale(scale: Double, affineTransformOp: AffineTransformOp): BufferedImage {
    val w = width
    val h = height
    val w2 = (w * scale).toInt()
    val h2 = (h * scale).toInt()
    val after = BufferedImage(w2, h2, type)
    affineTransformOp.filter(this, after)
    return after
}

fun BufferedImage.getScaleToBoundBy80(): Pair<Double, AffineTransformOp> {
    val maxDimenAny = 90.0
    val scale = minOf(maxDimenAny / width, maxDimenAny / height)
    return scale to generateScaledownTransform(scale)
}

private fun generateScaledownTransform(scale: Double) =
    AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), AffineTransformOp.TYPE_BICUBIC)


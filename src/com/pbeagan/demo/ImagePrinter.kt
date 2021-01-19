package com.pbeagan.demo

import com.pbeagan.demo.ImagePrinter.CompressionStyle.DOTS
import com.pbeagan.demo.ImagePrinter.CompressionStyle.UP_DOWN
import com.pbeagan.demo.TerminalColorStyle.Colors
import com.pbeagan.demo.TerminalColorStyle.colorIntStripAlpha
import com.pbeagan.demo.TerminalColorStyle.colorIntToARGB
import com.pbeagan.demo.TerminalColorStyle.style
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage


class ImagePrinter {
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
                    colorBackground = if (a == 0) Colors.Custom(g = 255) else Colors.Custom(r, g, b),
                    colorForeground = if (a1 == 0) Colors.Custom(g = 255) else Colors.Custom(r1, g1, b1)
                ).also { print(it) }
            }
            println()
        }
    }

    private fun Set<Int>.reduce(color: Int) = associateBy { each ->
        distance(
            color shr 16 and 255,
            color shr 8 and 255,
            color and 255,
            each shr 16 and 255,
            each shr 8 and 255,
            each and 255
        )
    }.minBy { it.key }?.value

    fun printImageReducedPalette(read: BufferedImage, paletteColors: Set<Int>?) {
        (read.minY until read.height).chunked(2).forEach { y ->
            (read.minX until read.width).forEach { x ->
                val i = y.getOrNull(0) ?: 0
                val i1 = y.getOrNull(1) ?: 0
                "▄".style(
                    colorBackground = this.applyPalette(read.getRGB(x, i), paletteColors),
                    colorForeground = this.applyPalette(read.getRGB(x, i1), paletteColors)
                ).also { print(it) }
                Unit
            }
            println()
        }
    }

    private val applyPalette = { rgb: Int, paletteColors: Set<Int>? ->
        val a = rgb.colorIntStripAlpha()
        val colorBackground = Colors.Custom((paletteColors?.reduce(a) ?: a).colorIntToARGB())
        colorBackground
    }.memoize()

    fun printImage(read: BufferedImage) {
        (read.minY until read.height).forEach { y ->
            (read.minX until read.width).forEach { x ->
                val (a, r, g, b) = read.getRGB(x, y).colorIntToARGB()
                "  ".style(
                    colorBackground = if (a == 0) {
                        Colors.Custom(g = 255)
                    } else {
                        Colors.Custom(r, g, b)
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

class Memoize1<in T, out R>(val f: (T) -> R) : (T) -> R {
    private val values = mutableMapOf<T, R>()
    override fun invoke(x: T): R {
        return values.getOrPut(x, { f(x) })
    }
}

class Memoize2<in S, in T, out R>(val f: (S, T) -> R) : (S, T) -> R {
    private val values = mutableMapOf<Pair<S, T>, R>()

    override fun invoke(p1: S, p2: T): R {
        return values.getOrPut(p1 to p2, { f(p1, p2) })
    }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)
fun <S, T, R> ((S, T) -> R).memoize(): (S, T) -> R = Memoize2(this)


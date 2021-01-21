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

    private fun Set<Int>.reduce(color: Int) = this.minBy { each ->
        distance(
            color shr 16 and 255,
            color shr 8 and 255,
            color and 255,
            each shr 16 and 255,
            each shr 8 and 255,
            each and 255
        )
    }

    fun printImageReducedPalette(read: BufferedImage, paletteColors: Set<Int>?) {

        val applyPalette = { rgb: Int, palette: Set<Int>? ->
            val a = rgb.colorIntStripAlpha()
            palette?.reduce(a) ?: a
        }.memoize()

        val set = Color256.values().toSet().drop(16).sortedBy { it.color }
        val reducedSetApplicator = { color: Int ->
            set
//                .associateBy { each ->
//
//            }
                .minBy { each ->
                    distance(
                        color shr 16 and 255,
                        color shr 8 and 255,
                        color and 255,
                        each.color shr 16 and 255,
                        each.color shr 8 and 255,
                        each.color and 255
                    )
                }

//            set.binarySearch { each ->
//                val x1 = color shr 16 and 255
//                val x2 = color shr 8 and 255
//                val x3 = color and 255
//                val y1 = each.color shr 16 and 255
//                val y2 = each.color shr 8 and 255
//                val y3 = each.color and 255
//
//                ((x1 + x2 + x3) / 3) - ((y1 + y2 + y3) / 3)
//            }.let { if (it < 0) -it else it }.let { set[it] }
        }.memoize()

        val toColor = { i: Color256? -> Colors.CustomPreset(i?.number ?: 0) }.memoize()

        (read.minY until read.height).chunked(2).forEach { y ->
            (read.minX until read.width).forEach { x ->
                val i = y.getOrNull(0) ?: 0
                val i1 = y.getOrNull(1) ?: 0
                val colorBackground = applyPalette(read.getRGB(x, i), paletteColors)
                val colorForeground = applyPalette(read.getRGB(x, i1), paletteColors)
                if (false) {
                    "▄".style(
                        colorBackground = reducedSetApplicator(colorBackground).let(toColor),
                        colorForeground = reducedSetApplicator(colorForeground).let(toColor)
                    ).also { print(it) }
                } else {
                    "▄".style(
                        colorBackground = colorBackground.let { Colors.Custom(it.colorIntToARGB()) },
                        colorForeground = colorForeground.let { Colors.Custom(it.colorIntToARGB()) }
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


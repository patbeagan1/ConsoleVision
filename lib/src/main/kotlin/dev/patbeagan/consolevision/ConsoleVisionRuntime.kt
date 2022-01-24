package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.TerminalColorStyle.CURSOR_TO_START
import dev.patbeagan.consolevision.util.createColorPalette
import dev.patbeagan.consolevision.util.getScaleToBoundBy
import dev.patbeagan.consolevision.util.scale
import java.awt.image.BufferedImage

class ConsoleVisionRuntime(
    private val file: BufferedImage?,
    paletteImage: BufferedImage?,
    reductionRate: Int,
    paletteReductionRate: Int,
    isCompatPalette: Boolean,
    val width: Int?,
    val height: Int?,
    shouldNormalize: Boolean,
) : Display {
    private val paletteColors: Set<Int>? = paletteImage?.createColorPalette(paletteReductionRate)
    private val imagePrinter = ImagePrinter(
        reductionRate,
        isCompatPalette,
        shouldNormalize
    )

    class ScaleTransform(image: BufferedImage, width: Int?, height: Int?) {
        val scaleTransform by lazy {
            image.getScaleToBoundBy(width, height)
        }
    }

    private var transform: ScaleTransform? = null
    private fun getTransform(image: BufferedImage): ScaleTransform =
        transform ?: ScaleTransform(image, width, height).also { transform = it }

    override fun printFrame(): String {
        val scaleTransform =
            file?.let { getTransform(it).scaleTransform } ?: return ""
        // todo make this an option
        print(CURSOR_TO_START)
        return imagePrinter.printImage(
            file.scale(
                scaleTransform.first,
                scaleTransform.second
            ),
            paletteColors
        )
    }
}

interface Display {
    fun printFrame(): String
}

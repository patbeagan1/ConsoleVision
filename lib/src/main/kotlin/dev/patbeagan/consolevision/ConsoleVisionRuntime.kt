package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.TerminalColorStyle.CURSOR_TO_START
import dev.patbeagan.consolevision.util.createColorPalette
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
) {
    private val paletteColors: Set<Int>? = paletteImage?.createColorPalette(paletteReductionRate)
    private val imagePrinter = ImagePrinter(
        reductionRate,
        isCompatPalette,
        shouldNormalize
    )
    private var transform: ScaleTransform? = null
    private fun getTransform(image: BufferedImage): ScaleTransform =
        transform ?: ScaleTransform(image, width, height).also { transform = it }

    fun printFrame(): String {
        val scaleTransform =
            file?.let { getTransform(it).scaleTransform } ?: return ""
        // todo make this an option
        print(CURSOR_TO_START)

        val scaledImage = file.scale(
            scaleTransform.first,
            scaleTransform.second
        )
        return imagePrinter.printImage(
            scaledImage,
            paletteColors
        )
    }
}


package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.TerminalColorStyle.CURSOR_TO_START
import dev.patbeagan.consolevision.util.createColorPalette
import dev.patbeagan.consolevision.util.getScaleToBoundBy
import dev.patbeagan.consolevision.util.scale
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ConsoleVisionRuntime(
    private val filename: String?,
    palette: String?,
    reductionRate: Int,
    paletteReductionRate: Int,
    isCompatPalette: Boolean,
    val width: Int?,
    val height: Int?,
    shouldNormalize: Boolean,
) : Display {
    private val paletteImage = palette?.let { ImageIO.read(File(palette)) }
    private val paletteColors: Set<Int>? = paletteImage?.let {
        it.createColorPalette(paletteReductionRate)
    }
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

    fun start() {
        printFrame(ImageIO.read(File(filename)))
    }

    override fun printFrame(image: BufferedImage) {
        val scaleTransform = getTransform(image).scaleTransform
        print(CURSOR_TO_START)
        imagePrinter.printImage(
            image.scale(
                scaleTransform.first,
                scaleTransform.second
            ),
            paletteColors
        )
    }
}

interface Display {
    fun printFrame(image: BufferedImage)
}

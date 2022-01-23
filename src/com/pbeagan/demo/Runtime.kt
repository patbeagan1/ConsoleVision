package com.pbeagan.demo

import com.pbeagan.demo.TerminalColorStyle.CURSOR_TO_START
import com.pbeagan.demo.util.createColorPalette
import com.pbeagan.demo.util.getScaleToBoundBy
import com.pbeagan.demo.util.scale
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Runtime(
    private val filename: String?,
    palette: String?,
    reductionRate: Int,
    paletteReductionRate: Int,
    isCompatPalette: Boolean,
    val width: Int?,
    val height: Int?,
    shouldNormalize: Boolean
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


    fun run() {
//        PlayerVideo(filename, this).run()
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

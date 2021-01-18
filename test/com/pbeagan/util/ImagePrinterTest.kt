package com.pbeagan.util

import com.pbeagan.demo.ImagePrinter
import com.pbeagan.demo.convertToBufferedImage
import org.junit.Before
import org.junit.Test
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

internal class ImagePrinterTest {
    lateinit var imagePrinter: ImagePrinter

    @Before
    fun setup() {
        imagePrinter = ImagePrinter()
    }

    @Test
    fun `sample image fullsize`() {
        imagePrinter.printImage(ImageIO.read(File(IMAGE_SAMPLE)))
    }

    @Test
    fun `sample image compressed`() {
        imagePrinter.printImageCompressed(ImageIO.read(File(IMAGE_SAMPLE)))
    }

    @Test
    fun `sample image compressed dots`() {
        imagePrinter.printImageCompressed(
            ImageIO.read(File(IMAGE_SAMPLE)),
            ImagePrinter.CompressionStyle.DOTS
        )
    }

    @Test
    fun `sizedown sampling comparison`() {
        val read = ImageIO.read(File(IMAGE_SAMPLE2))
        listOf(
            Image.SCALE_DEFAULT,
            Image.SCALE_FAST,
            Image.SCALE_SMOOTH,
            Image.SCALE_REPLICATE,
            Image.SCALE_AREA_AVERAGING
        ).forEach {
            val width = read.width
            val height = read.height
            val aspectRatio = width.toDouble() / height.toDouble()
            val maxDimen = 100
            read.getScaledInstance((maxDimen * aspectRatio).toInt(), maxDimen, it)
                .convertToBufferedImage()
                ?.also { bufferedImage ->
                    imagePrinter.printImageCompressed(bufferedImage,
                        ImagePrinter.CompressionStyle.DOTS)
                }
        }
    }

    companion object {
        const val IMAGE_SAMPLE = "/Users/pbeagan/Downloads/charizard.png"
        const val IMAGE_SAMPLE2 = "/Users/pbeagan/Downloads/lenna.png"
    }
}
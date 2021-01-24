package com.pbeagan.util

import com.pbeagan.demo.ImagePrinter
import com.pbeagan.demo.util.getScaleToBoundBy
import com.pbeagan.demo.util.scale
import org.junit.Before
import org.junit.Test
import java.io.File
import javax.imageio.ImageIO

internal class ImagePrinterTest {
    lateinit var imagePrinter: ImagePrinter

    @Before
    fun setup() {
        imagePrinter = ImagePrinter(0, false)
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

        val (scale, transformOp) = read.getScaleToBoundBy(90, 90)
        imagePrinter.printImageCompressed(
            read.scale(scale, transformOp),
            ImagePrinter.CompressionStyle.UP_DOWN
        )
    }

    companion object {
        const val IMAGE_SAMPLE = "/Users/pbeagan/Downloads/charizard.png"
        const val IMAGE_SAMPLE2 = "/Users/pbeagan/Downloads/lenna.png"
    }
}
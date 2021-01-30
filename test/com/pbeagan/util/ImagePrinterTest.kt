package com.pbeagan.util

import com.pbeagan.demo.ImagePrinter
import com.pbeagan.demo.util.getScaleToBoundBy
import com.pbeagan.demo.util.scale
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal class ImagePrinterTest {
    lateinit var imagePrinter: ImagePrinter

    @Before
    fun setup() {
        imagePrinter = ImagePrinter(0, false, shouldNormalize = false)
    }

    @Test
    fun `sample image fullsize`() {
        imagePrinter.printImage(ImageIO.read(File(IMAGE_SAMPLE)))
    }

    @Test
    fun `sample image fullsize normalized`() {
        ImagePrinter(0, false, shouldNormalize = true).printImage(ImageIO.read(File(IMAGE_SAMPLE)))
    }

    @Test
    fun `sample image reduced normalized`() {
        val read = ImageIO.read(File(IMAGE_SAMPLE2))

        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        ImagePrinter(0, false, shouldNormalize = true).printImage(read1)
        ImagePrinter(0, false, shouldNormalize = false).printImage(read2)
    }

    private fun getScaledImage(read: BufferedImage): BufferedImage {
        val (scale, transformOp) = read.getScaleToBoundBy(40, 40)
        val read1 = read.scale(scale, transformOp)
        return read1
    }

    @Test
    fun `sample image compressed`() {
        imagePrinter.printImage(ImageIO.read(File(IMAGE_SAMPLE)), compressionStyle = ImagePrinter.CompressionStyle.DOTS)
    }

    @Test
    fun `sample image compressed dots`() {
        imagePrinter.printImage(
            ImageIO.read(File(IMAGE_SAMPLE))
        )
    }

    @Test
    fun `sizedown sampling comparison`() {
        val read = ImageIO.read(File(IMAGE_SAMPLE2))

        val (scale, transformOp) = read.getScaleToBoundBy(90, 90)
        imagePrinter.printImage(
            read.scale(scale, transformOp)
        )
    }

    companion object {
        const val IMAGE_SAMPLE = "/Users/pbeagan/Downloads/charizard.png"
        const val IMAGE_SAMPLE2 = "/Users/pbeagan/Downloads/lenna.png"
    }
}

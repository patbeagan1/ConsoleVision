package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.ImagePrinter
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOException
import javax.imageio.ImageIO

internal class ImagePrinterTest {
    lateinit var imagePrinter: ImagePrinter

    private fun readAsset(s: String): BufferedImage = ImageIO.read(File("../assets/$s"))

    @Before
    fun setup() {
        imagePrinter = ImagePrinter(0, false, shouldNormalizeColors = false)
    }

    @Test
    fun `sample image fullsize`() {
        val directory = File("./")
        println(directory.absolutePath)
        imagePrinter.printImage(readAsset(IMAGE_SMALL))
    }

    @Test
    fun `sample image fullsize normalized`() {
        ImagePrinter(0, false, shouldNormalizeColors = true)
            .printImage(readAsset(IMAGE_SMALL))
    }

    @Test
    fun `sample image reduced normalized`() {
        val read = readAsset(LENNA)

        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Normalized: TRUE")
        ImagePrinter(0, false, shouldNormalizeColors = true).printImage(read1)
        println("Normalized: FALSE")
        ImagePrinter(0, false, shouldNormalizeColors = false).printImage(read2)
    }

    private fun getScaledImage(read: BufferedImage): BufferedImage {
        val (scale, transformOp) = read.getScaleToBoundBy(40, 40)
        val read1 = read.scale(scale, transformOp)
        return read1
    }

    @Test
    fun `sample image compressed dots`() {
        imagePrinter.printImage(
            readAsset(IMAGE_SMALL),
            compressionStyle = ImagePrinter.CompressionStyle.DOTS
        )
    }

    @Test
    fun `sample image compressed`() {
        imagePrinter.printImage(
            readAsset(IMAGE_SMALL)
        )
    }

    @Test
    fun `sizedown sampling comparison`() {
        val read = readAsset(IMAGE_LARGE)

        val (scale, transformOp) = read.getScaleToBoundBy(90, 90)
        imagePrinter.printImage(
            read.scale(scale, transformOp)
        )
    }

    @Test
    fun `palette swapping`() {
        val read = readAsset(LENNA)
        listOf(
            "borkfest-1x.png",
            "denim-1x.png",
            "island-life-1x.png",
            "neon-lights-floatingtable-1x.png",
            "oil-6-1x.png",
            "pear36-1x.png",
            "power-38-1x.png",
            "rewild-64-1x.png",
            "slso8-1x.png",
            "softdecay-gb-1x.png"
        ).map {
            it to try {
                readAsset("palettes/$it")
            } catch (e: IIOException) {
                println(it)
                println(e)
                null
            }
        }.forEach {
            val (scale, transformOp) = read.getScaleToBoundBy(70, 70)
            println(it.first)
            imagePrinter.printImage(
                read.scale(scale, transformOp),
                it.second?.createColorPalette(0)
            )
        }
    }

    companion object {
        const val IMAGE_LARGE = "Landscape.jpg"
        const val IMAGE_SMALL = "LandscapeSmall.jpeg"
        const val LENNA = "Lenna.png"
    }
}

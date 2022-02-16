package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.ColorMapToAnsi
import dev.patbeagan.consolevision.ImagePrinter
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.getScaleToBoundBy
import dev.patbeagan.consolevision.scale
import dev.patbeagan.consolevision.types.CompressionStyle
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
        imagePrinter = ImagePrinter(0, ColorMapToAnsi(false), shouldNormalizeColors = false)
    }

    @Test
    fun compatPalette() {
        println(
            ImagePrinter(0, ColorMapToAnsi(true), shouldNormalizeColors = false).getFrame(
                getScaledImage(readAsset(LENNA))
            )
        )
    }

    @Test
    fun `sample image fullsize`() {
        val directory = File("./")
        println(directory.absolutePath)
        imagePrinter.getFrame(readAsset(IMAGE_SMALL)).also { println(it) }
    }

    @Test
    fun `sample image fullsize normalized`() {
        ImagePrinter(0, ColorMapToAnsi(false), shouldNormalizeColors = true)
            .getFrame(readAsset(IMAGE_SMALL)).also { println(it) }
    }

    @Test
    fun `sample image scaled normalized`() {
        val read = readAsset(LENNA)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Normalized: TRUE")
        ImagePrinter(0, ColorMapToAnsi(false), shouldNormalizeColors = true).getFrame(read1)
            .also { println(it) }
        println("Normalized: FALSE")
        ImagePrinter(0, ColorMapToAnsi(false), shouldNormalizeColors = false).getFrame(read2)
            .also { println(it) }
    }

    @Test
    fun `sample image reduced - gradient`() {
        val read = readAsset(GRADIENT)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Reduced: TRUE")
        ImagePrinter(8, ColorMapToAnsi(false), shouldNormalizeColors = false).getFrame(read1)
            .also { println(it) }
        println("Reduced: FALSE")
        ImagePrinter(0, ColorMapToAnsi(false), shouldNormalizeColors = false).getFrame(read2)
            .also { println(it) }
    }

    @Test
    fun `sample image reduced - lenna`() {
        val read = readAsset(LENNA)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Reduced: TRUE")
        ImagePrinter(40, ColorMapToAnsi(false), shouldNormalizeColors = false).getFrame(read1)
            .also { println(it) }
        println("Reduced: FALSE")
        ImagePrinter(0, ColorMapToAnsi(false), shouldNormalizeColors = false).getFrame(read2)
            .also { println(it) }
    }

    private fun getScaledImage(read: BufferedImage): BufferedImage {
        val (scale, transformOp) = read.getScaleToBoundBy(40, 40)
        return read.scale(scale, transformOp)
    }

    @Test
    fun `sample image compressed dots`() {
        imagePrinter.getFrame(
            readAsset(IMAGE_SMALL),
            compressionStyle = CompressionStyle.DOTS_HIGH
        ).also { println(it) }
    }

    @Test
    fun `sample image compressed`() {
        imagePrinter.getFrame(
            readAsset(IMAGE_SMALL)
        ).also { println(it) }
    }

    @Test
    fun `color mutation filter`() {
        val image = ImageScaler(80, 80).scaledImage(readAsset(LENNA))
        image ?: run {
            print("Image not found")
            return
        }
        ImagePrinter(
            0,
            ColorMapToAnsi(false),
            shouldNormalizeColors = false,
            shouldMutateColors = true
        ).getFrame(image)
            .also { println(it) }
    }

    @Test
    fun `sizedown sampling comparison`() {
        val read = readAsset(IMAGE_LARGE)

        val (scale, transformOp) = read.getScaleToBoundBy(90, 90)
        imagePrinter.getFrame(
            read.scale(scale, transformOp)
        ).also { println(it) }
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
        }.forEach { (first, second) ->
            val (scale, transformOp) = read.getScaleToBoundBy(70, 70)
            println(first)
            imagePrinter.getFrame(
                read.scale(scale, transformOp),
                second?.createColorPalette(0)
            ).also { println(it) }
        }
    }

    companion object {
        const val IMAGE_LARGE = "Landscape.jpg"
        const val IMAGE_SMALL = "LandscapeSmall.jpeg"
        const val LENNA = "Lenna.png"
        const val GRADIENT = "purple-green-gradient.png"
    }
}

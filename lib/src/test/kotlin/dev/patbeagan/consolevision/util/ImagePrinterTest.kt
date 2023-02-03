package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.ColorConverter
import dev.patbeagan.consolevision.FramePrinter
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.getScaleToBoundBy
import dev.patbeagan.consolevision.imagefilter.ColorMutation
import dev.patbeagan.consolevision.imagefilter.ColorNormalization
import dev.patbeagan.consolevision.scale
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.toList2D
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.List2D
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOException
import javax.imageio.ImageIO

internal class ImagePrinterTest {
    lateinit var framePrinter: FramePrinter

    private fun readAsset(s: String): BufferedImage = ImageIO
        .read(File("../assets/$s"))

    @Before
    fun setup() {
        framePrinter = FramePrinter(
            reductionRate = 0,
        )
    }

    @Test
    fun compatPalette() {
        println(
            FramePrinter(
                0,
                colorConverter = ColorConverter.CompatColorConverter(),
            ).getFrame(
                getScaledImage(readAsset(Mona))
            )
        )
    }

    @Test
    fun `sample image fullsize`() {
        println("sample image fullsize")
        val directory = File("./")
        println(directory.absolutePath)
        framePrinter.getFrame(readAsset(ImageSmall).toList2D()).also { println(it) }
    }

    @Test
    fun `sample image fullsize normalized`() {
        println("sample image fullsize normalized")
        FramePrinter(
            reductionRate = 0,
            filters = listOf(
                ColorNormalization()
            )
        )
            .getFrame(readAsset(ImageSmall).toList2D()).also { println(it) }
    }

    @Test
    fun `sample image scaled normalized`() {
        println("sample image scaled normalized")
        val read = readAsset(Mona)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Normalized: TRUE")
        FramePrinter(
            reductionRate = 0,
            filters = listOf(ColorNormalization()),
        ).getFrame(read1)
            .also { println(it) }
        println("Normalized: FALSE")
        FramePrinter(
            reductionRate = 0,
        ).getFrame(read2)
            .also { println(it) }
    }

    @Test
    fun `sample image reduced - gradient`() {
        println("sample image reduced - gradient")
        val read = readAsset(Gradient)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Reduced: TRUE")
        FramePrinter(
            8,
        ).getFrame(read1)
            .also { println(it) }
        println("Reduced: FALSE")
        FramePrinter(
            reductionRate = 0,
        ).getFrame(read2)
            .also { println(it) }
    }

    @Test
    fun `sample image reduced - lenna`() {
        println("sample image reduced - lenna")
        val read = readAsset(Mona)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Reduced: TRUE")
        FramePrinter(
            40,
        ).getFrame(read1)
            .also { println(it) }
        println("Reduced: FALSE")
        FramePrinter(
            reductionRate = 0,
        ).getFrame(read2)
            .also { println(it) }
    }

    private fun getScaledImage(read: BufferedImage): List2D<ColorInt> {
        val (scale, transformOp) = read.getScaleToBoundBy(40, 40)
        return read.scale(scale, transformOp).toList2D()
    }

    @Test
    fun `sample image compressed dots`() {
        println("sample image compressed dots")
        FramePrinter(
            reductionRate = 0,
            compressionStyle = CompressionStyle.DOTS_HIGH
        ).getFrame(
            readAsset(ImageSmall).toList2D(),
        ).also { println(it) }
    }

    @Test
    fun `sample image compressed`() {
        println("sample image compressed")
        framePrinter.getFrame(
            readAsset(ImageSmall).toList2D()
        ).also { println(it) }
    }

    @Test
    fun `color mutation filter`() {
        println("color mutation filter")
        val image = ImageScaler(80, 80).scaledImage(readAsset(Mona))
        image ?: run {
            print("Image not found")
            return
        }
        FramePrinter(
            reductionRate = 0,
            colorConverter = ColorConverter.CompatColorConverter(),
            filters = listOf(
                ColorNormalization(),
                ColorMutation(50)
            )
        ).getFrame(image.toList2D())
            .also { println(it) }
    }

    @Test
    fun `sizedown sampling comparison`() {
        println("sizedown sampling comparison")
        val read = readAsset(ImageLarge)

        val (scale, transformOp) = read.getScaleToBoundBy(90, 90)
        framePrinter.getFrame(
            read.scale(scale, transformOp).toList2D()
        ).also { println(it) }
    }

    @Test
    fun `palette swapping`() {
        println("palette swapping")
        val read = readAsset(Mona)
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
            FramePrinter(
                reductionRate = 0,
                compressionStyle = CompressionStyle.UPPER_HALF,
                paletteColors = second?.toList2D()?.let { ColorPalette.from(it, 0) },
            ).getFrame(
                read.scale(scale, transformOp).toList2D()
            ).also { println(it) }
        }
    }

    companion object {
        const val ImageLarge = "Landscape.jpg"
        const val ImageSmall = "LandscapeSmall.jpeg"
        const val Mona = "mona-lisa.jpeg"
        const val Gradient = "purple-green-gradient.png"
    }
}

package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.ColorConverter
import dev.patbeagan.consolevision.FrameProvider
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.getScaleToBoundBy
import dev.patbeagan.consolevision.imagefilter.FilterColorMutation
import dev.patbeagan.consolevision.imagefilter.FilterColorNormalization
import dev.patbeagan.consolevision.imagefilter.FilterColorPalette
import dev.patbeagan.consolevision.imagefilter.FilterReducedColorSpace
import dev.patbeagan.consolevision.scale
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.toList2D
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.List2D
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOException
import javax.imageio.ImageIO

internal class ImagePrinterTest {

    private fun readAsset(s: String): BufferedImage = ImageIO
        .read(File("../assets/$s"))

    @Test
    fun compatPalette() {
        println(
            FrameProvider(colorConverter = ColorConverter.CompatColorConverter())
                .getFrame(getScaledImage(readAsset(Mona)))
        )
    }

    @Test
    fun `sample image fullsize`() {
        println("sample image fullsize")
        val directory = File("./")
        println(directory.absolutePath)
        FrameProvider()
            .getFrame(
                readAsset(ImageSmall).toList2D()
            ).also { println(it) }
    }

    @Test
    fun `sample image fullsize normalized`() {
        println("sample image fullsize normalized")
        FrameProvider(
            filters = listOf(
                FilterColorNormalization()
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
        FrameProvider(
            filters = listOf(FilterColorNormalization()),
        ).getFrame(read1)
            .also { println(it) }
        println("Normalized: FALSE")
        FrameProvider(
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
        FrameProvider(
            filters = listOf(FilterReducedColorSpace(8))
        ).getFrame(read1).also { println(it) }
        println("Reduced: FALSE")
        FrameProvider(
        ).getFrame(read2)
            .also { println(it) }
    }

    @Test
    fun `sample image reduced - mona`() {
        println("sample image reduced - mona")
        val read = readAsset(Mona)
        val read1 = getScaledImage(read)
        val read2 = getScaledImage(read)

        println("Reduced: TRUE")
        FrameProvider(
            filters = listOf(FilterReducedColorSpace(40)),
        ).getFrame(read1)
            .also { println(it) }
        println("Reduced: FALSE")
        FrameProvider(
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
        FrameProvider(
            compressionStyle = CompressionStyle.DOTS_HIGH
        ).getFrame(
            readAsset(ImageSmall).toList2D(),
        ).also { println(it) }
    }

    @Test
    fun `sample image compressed`() {
        println("sample image compressed")
        FrameProvider().getFrame(
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
        FrameProvider(
            colorConverter = ColorConverter.CompatColorConverter(),
            filters = listOf(
                FilterColorNormalization(),
                FilterColorMutation(50)
            )
        ).getFrame(image.toList2D())
            .also { println(it) }
    }

    @Test
    fun `sizedown sampling comparison`() {
        println("sizedown sampling comparison")
        val read = readAsset(ImageLarge)

        val (scale, transformOp) = read.getScaleToBoundBy(90, 90)
        FrameProvider().getFrame(
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
        }.forEach { (name, image) ->
            image!!
            val (scale, transformOp) = read.getScaleToBoundBy(70, 70)
            println(name)
            FrameProvider(
                compressionStyle = CompressionStyle.UPPER_HALF,
                filters = listOf(
                    FilterColorPalette(
                        image.toList2D().let { ColorPalette.from(it, 0) }
                    )
                )
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

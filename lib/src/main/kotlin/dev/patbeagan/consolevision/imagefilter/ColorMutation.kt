package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.types.ColorInt
import java.awt.image.BufferedImage
import kotlin.random.Random

/**
 * A filter that adds some color noise to an image by
 * mutating the color of each pixel a certain amount.
 */
class ColorMutation(private val variance: Int) : ImageFilter {
    override fun invoke(bufferedImage: BufferedImage) {
        bufferedImage.withLine { y, x ->
            ColorInt(bufferedImage.getRGB(x, y))
                .mutate(variance)
                .let { bufferedImage.setRGB(x, y, it.color) }
        }
    }

    private fun ColorInt.mutate(variance: Int): ColorInt {
        fun Int.newColorVal() = this + (Random.nextInt() % variance).coerceIn(0..255)
        return ColorInt.from(
            0,
            this.colorRed.newColorVal(),
            this.colorGreen.newColorVal(),
            this.colorBlue.newColorVal()
        )
    }
}

package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.util.ColorIntHelper
import java.awt.image.BufferedImage

/**
 * A filter that will go through the image and calculate the max and min values
 * of each color - red, green, blue. It will then normalize the colors, so that
 * they take up the entire available space.
 *
 * For example, if we looked at the red values of a couple of colors, we might get
 * - 10
 * - 15
 * - 20
 * - 30
 *
 * However, red is allowed to go anywhere from 0 to 256.
 * Normalizing red would reassign these colors to
 * - 0
 * - 64
 * - 128
 * - 256
 *
 * so that their proportions amongst themselves remain correct,
 * but they take up all of the available space.
 */
class ColorNormalization : ImageFilter {
    override fun invoke(bufferedImage: BufferedImage) {
        var minR = Integer.MAX_VALUE
        var minG = Integer.MAX_VALUE
        var minB = Integer.MAX_VALUE
        var maxR = Integer.MIN_VALUE
        var maxG = Integer.MIN_VALUE
        var maxB = Integer.MIN_VALUE

        bufferedImage.withLine { y, x ->
            val rgb = ColorInt.from(bufferedImage.getRGB(x, y))

            minR = if (rgb.colorRed < minR) rgb.colorRed else minR
            maxR = if (rgb.colorRed > maxR) rgb.colorRed else maxR

            minG = if (rgb.colorGreen < minG) rgb.colorGreen else minG
            maxG = if (rgb.colorGreen > maxG) rgb.colorGreen else maxG

            minB = if (rgb.colorBlue < minB) rgb.colorBlue else minB
            maxB = if (rgb.colorBlue > maxB) rgb.colorBlue else maxB
        }

        bufferedImage.withLine { y, x ->
            val newColor = bufferedImage.getRGB(x, y).let {
                val color = ColorInt.from(it)
                val toDouble = (color.colorRed - minR).toDouble()
                val i = maxR - minR
                val colorRed = toDouble / i
                val colorGreen = (color.colorGreen - minG).toDouble() / (maxG - minG)
                val colorBlue = (color.colorBlue - minB).toDouble() / (maxB - minB)

                ColorIntHelper.combineColor(
                    color.colorAlpha,
                    (colorRed * 255).toInt(),
                    (colorGreen * 255).toInt(),
                    (colorBlue * 255).toInt()
                )
            }
            bufferedImage.setRGB(x, y, newColor)
        }
    }

    private inline fun BufferedImage.withLine(
        onLineEnd: () -> Unit = {},
        action: (Int, Int) -> Unit,
    ) {
        (minY until height).forEach { y ->
            (minX until width).forEach { x -> action(y, x) }
            onLineEnd()
        }
    }
}
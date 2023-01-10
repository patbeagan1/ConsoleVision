package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.types.List2D

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
    override fun invoke(image: List2D<ColorInt>) {
        var minR = Int.MAX_VALUE
        var minG = Int.MAX_VALUE
        var minB = Int.MAX_VALUE
        var maxR = Int.MIN_VALUE
        var maxG = Int.MIN_VALUE
        var maxB = Int.MIN_VALUE

        image.traverseMutate { _, _, color ->

            minR = if (color.colorRed < minR) color.colorRed else minR
            maxR = if (color.colorRed > maxR) color.colorRed else maxR

            minG = if (color.colorGreen < minG) color.colorGreen else minG
            maxG = if (color.colorGreen > maxG) color.colorGreen else maxG

            minB = if (color.colorBlue < minB) color.colorBlue else minB
            maxB = if (color.colorBlue > maxB) color.colorBlue else maxB

            val colorRed = (color.colorRed - minR).toDouble() / (maxR - minR)
            val colorGreen = (color.colorGreen - minG).toDouble() / (maxG - minG)
            val colorBlue = (color.colorBlue - minB).toDouble() / (maxB - minB)

            ColorInt.from(
                color.colorAlpha,
                (colorRed * 255).toInt(),
                (colorGreen * 255).toInt(),
                (colorBlue * 255).toInt()
            )
        }
    }
}

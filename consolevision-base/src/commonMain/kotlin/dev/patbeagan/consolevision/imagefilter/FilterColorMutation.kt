package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.List2D
import kotlin.random.Random

/**
 * A filter that adds some color noise to an image by
 * mutating the color of each pixel a certain amount.
 */
class FilterColorMutation(private val variance: Int) : ImageFilter {
    override fun invoke(image: List2D<ColorInt>) {
        image.traverseMutate { _, _, color ->
            color.mutate(variance)
        }
    }

    private fun Int.newColorVal() = this + (Random.nextInt() % variance).coerceIn(0..255)

    private fun ColorInt.mutate(variance: Int): ColorInt = ColorInt.from(
        0,
        this.colorRed.newColorVal(),
        this.colorGreen.newColorVal(),
        this.colorBlue.newColorVal()
    )
}

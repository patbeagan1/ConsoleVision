package dev.patbeagan.consolevision.imagefilter

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.List2D

class FilterReducedColorSpace(val reductionRate: Int) : ImageFilter {
    override fun invoke(image: List2D<ColorInt>) = image
        .traverseMutate { _, _, each ->
            each.reduceColorSpaceBy(reductionRate)
        }

    companion object {
        fun ColorInt.reduceColorSpaceBy(factor: Int): ColorInt {
            if (factor < 1) return this // don't want to divide by 0
            val r = color shr 16 and 0xff
            val g = color shr 8 and 0xff
            val b = color and 0xff
            val r2 = (r / factor) * factor
            val g2 = (g / factor) * factor
            val b2 = (b / factor) * factor
            return ColorInt((r2 shl 16) + (g2 shl 8) + b2)
        }
    }
}
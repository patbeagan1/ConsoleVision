package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.CompressedPoint
import dev.patbeagan.consolevision.types.List2D

interface IFrameProvider {
    fun getFrame(frame: List2D<ColorInt>): String
    fun getFrame(
        frame: List2D<ColorInt>,
        charList2D: Map<CompressedPoint, Printable>?
    ): String
}
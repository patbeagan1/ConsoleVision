package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.ColorMutation
import dev.patbeagan.consolevision.imagefilter.ColorNormalization
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Special
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.List2D

class ConsoleVisionRuntime(
    paletteImage: List2D<ColorInt>?,
    config: Config
) {

    private val framePrinter: FramePrinter = FramePrinter(
        config.reductionRate,
        paletteImage?.let {
            ColorPalette.from(it, config.paletteReductionRate)
        },
        buildList {
            if (config.shouldMutateColors) {
                add(ColorMutation(50))
            }
            if (config.shouldNormalize) {
                add(ColorNormalization())
            }
        },
        if (config.isCompatPalette) {
            ColorConverter.CompatColorConverter()
        } else {
            ColorConverter.NormalColorConverter()
        }
    )

    fun printFrame(file: List2D<ColorInt>): String {
        // todo make this an option
        print(Special.cursorToPosition(1, 1))
        return framePrinter.getFrame(file)
    }

    data class Config(
        val reductionRate: Int,
        val paletteReductionRate: Int,
        val isCompatPalette: Boolean,
        val shouldNormalize: Boolean,
        val shouldMutateColors: Boolean = false
    )
}

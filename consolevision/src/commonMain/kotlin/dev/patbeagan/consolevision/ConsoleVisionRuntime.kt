package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.List2D
import dev.patbeagan.consolevision.util.createColorPalette

class ConsoleVisionRuntime(
    paletteImage: List2D<ColorInt>?,
    config: Config
) {

    private val paletteColors: ColorPalette? = paletteImage
        ?.createColorPalette(config.paletteReductionRate)

    private val framePrinter = FramePrinter(
        config.reductionRate,
        ColorMapToAnsi(config.isCompatPalette),
        config.shouldNormalize,
        config.shouldMutateColors,
        paletteColors,
    )

    fun printFrame(file: List2D<ColorInt>): String {
//        // todo make this an option
//        print(CURSOR_TO_START)
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

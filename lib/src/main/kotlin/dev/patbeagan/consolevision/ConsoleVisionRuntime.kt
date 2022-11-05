package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.util.createColorPalette
import java.awt.image.BufferedImage

class ConsoleVisionRuntime(
    paletteImage: BufferedImage?,
    config: Config
) {

    private val paletteColors: ColorPalette? = paletteImage
        ?.createColorPalette(config.paletteReductionRate)

    private val imagePrinter = ImagePrinter(
        config.reductionRate,
        ColorMapToAnsi(config.isCompatPalette),
        config.shouldNormalize,
        config.shouldMutateColors,
        paletteColors,
    )

    fun printFrame(file: BufferedImage): String {
//        // todo make this an option
//        print(CURSOR_TO_START)
        return imagePrinter.getFrame(file)
    }

    data class Config(
        val reductionRate: Int,
        val paletteReductionRate: Int,
        val isCompatPalette: Boolean,
        val shouldNormalize: Boolean,
        val shouldMutateColors: Boolean = false
    )
}

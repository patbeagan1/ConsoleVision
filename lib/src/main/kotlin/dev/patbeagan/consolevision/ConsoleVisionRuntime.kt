package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.TerminalColorStyle.CURSOR_TO_START
import dev.patbeagan.consolevision.util.createColorPalette
import java.awt.image.BufferedImage

class ConsoleVisionRuntime(
    paletteImage: BufferedImage?,
    reductionRate: Int,
    paletteReductionRate: Int,
    isCompatPalette: Boolean,
    shouldNormalize: Boolean,
) {
    private val paletteColors: Set<Int>? = paletteImage?.createColorPalette(paletteReductionRate)
    private val imagePrinter = ImagePrinter(
        reductionRate,
        isCompatPalette,
        shouldNormalize
    )

    fun printFrame(file: BufferedImage): String {
        // todo make this an option
        print(CURSOR_TO_START)
        return imagePrinter.getFrame(
            file,
            paletteColors
        )
    }
}


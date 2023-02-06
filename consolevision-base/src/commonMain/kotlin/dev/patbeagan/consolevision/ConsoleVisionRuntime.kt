package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.FilterColorMutation
import dev.patbeagan.consolevision.imagefilter.FilterColorNormalization
import dev.patbeagan.consolevision.imagefilter.FilterColorPalette
import dev.patbeagan.consolevision.imagefilter.FilterReducedColorSpace
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.List2D

class ConsoleVisionRuntime(
    config: Config,
    paletteImage: List2D<ColorInt>? = null
) : IFrameProvider by FrameProvider(
    compressionStyle = CompressionStyle.UPPER_HALF,
    colorConverter = if (config.isCompatPalette) {
        ColorConverter.CompatColorConverter()
    } else {
        ColorConverter.NormalColorConverter()
    },
    filters = buildList {
        if (config.shouldMutateColors) {
            add(FilterColorMutation(50))
        }
        if (config.shouldNormalize) {
            add(FilterColorNormalization())
        }
        if (config.reductionRate != 0) {
            add(FilterReducedColorSpace(config.reductionRate))
        }
        if (paletteImage != null) {
            add(
                FilterColorPalette(
                    ColorPalette.from(
                        paletteImage,
                        config.paletteReductionRate
                    )
                )
            )
        }
    }
) {

    data class Config(
        val reductionRate: Int = 0,
        val paletteReductionRate: Int = 0,
        val isCompatPalette: Boolean = false,
        val shouldNormalize: Boolean = false,
        val shouldMutateColors: Boolean = false
    )
}

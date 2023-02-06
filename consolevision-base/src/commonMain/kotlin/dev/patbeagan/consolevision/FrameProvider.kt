package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.ImageFilter
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.ConsoleVision
import dev.patbeagan.consolevision.style.style
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.List2D

class FrameProvider(
    val compressionStyle: CompressionStyle = CompressionStyle.UPPER_HALF,
    val colorConverter: ColorConverter = ColorConverter.NormalColorConverter(),
    val filters: List<ImageFilter> = listOf()
) : IFrameProvider {

    override fun getFrame(frame: List2D<ColorInt>): String {
        // todo make this an option
        print(ConsoleVision.Special.cursorToPosition(1, 1))
        filters.forEach { it.invoke(frame) }

        val out = StringBuilder()
        val chunked = (0 until frame.height).chunked(2)
        // since there will be 2 colors printed per row,
        // we actually have 2 y values per row.
        // this part decides which one will be
        // the foreground vs the background.
        chunked.forEach { y ->
            (0 until frame.width).forEach { x ->
                val p1 = y.getOrNull(1)
                val foreground = y.getOrNull(0) ?: p1
                val background = if (foreground != p1) p1 else null
                compressionStyle.symbol.toString().style(
                    colorForeground = frame.convert(foreground, x),
                    colorBackground = frame.convert(background, x),
                ).also { out.append(it) }
            }
            out.append("\n")
        }
        return out.toString()
    }

    private fun List2D<ColorInt>.convert(y: Int?, x: Int) =
        colorConverter.convert(y?.let { at(x, it) })
}
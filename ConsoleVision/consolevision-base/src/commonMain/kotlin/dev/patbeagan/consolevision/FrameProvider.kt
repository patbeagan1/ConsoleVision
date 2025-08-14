package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.ImageFilter
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.ConsoleVision
import dev.patbeagan.consolevision.style.style
import dev.patbeagan.consolevision.types.CompressedPoint
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.List2D
import dev.patbeagan.consolevision.types.coord

class FrameProvider(
    val compressionStyle: CompressionStyle = CompressionStyle.UPPER_HALF,
    val colorConverter: ColorConverter = ColorConverter.NormalColorConverter(),
    val filters: List<ImageFilter> = listOf()
) : IFrameProvider {
    override fun getFrame(frame: List2D<ColorInt>): String = getFrame(frame, null)

    override fun getFrame(
        frame: List2D<ColorInt>,
        charList2D: Map<CompressedPoint, Printable>?
    ): String { // todo make this an option
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
                val cc = frame.renderedText(foreground, charList2D, x, background)
                    ?: frame.renderedCompressionCharacter(foreground, x, background)
                cc.also { out.append(it) }
            }
            out.append("\n")
        }
        return out.toString()
    }

    private fun List2D<ColorInt>.renderedText(
        foreground: Int?,
        charList2D: Map<CompressedPoint, Printable>?,
        x: Int,
        background: Int?
    ): String? {
        foreground ?: return null
        charList2D ?: return null
        val printable = charList2D[x coord foreground] ?: return null

        val colorForeground = printable.foreground
            ?.let { colorConverter.convert(it) }
            ?: convert(foreground, x)

        val colorBackground = printable.background
            ?.let { colorConverter.convert(it) }
            ?: convert(background, x)

        return printable.character.toString().style(
            colorForeground = colorForeground,
            colorBackground = colorBackground,
        )
    }

    private fun List2D<ColorInt>.renderedCompressionCharacter(
        foreground: Int?,
        x: Int,
        background: Int?
    ) = compressionStyle.symbol.toString().style(
        colorForeground = convert(foreground, x),
        colorBackground = convert(background, x),
    )

    private fun List2D<ColorInt>.convert(y: Int?, x: Int) =
        colorConverter.convert(y?.let { at(x, it) })
}

data class Printable(
    val character: Char,
    val foreground: ColorInt?,
    val background: ColorInt?
)
package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.ansi.AnsiConstants.CSI
import dev.patbeagan.consolevision.ansi.AnsiSGR

/**
 * Defines the style for a single character
 */
data class TerminalStyle(
    /**
     * The foreground color of this character
     */
    val colorForeground: AnsiColor = AnsiColor.Default,
    /**
     * The background color of this character
     */
    val colorBackground: AnsiColor = AnsiColor.Default,
    /**
     * The graphics applied to this character. See [AnsiSGR] for more info.
     */
    val sgr: AnsiSGR = AnsiSGR.Normal
)

fun String.style(
    style: TerminalStyle
): String = this.style(style.colorForeground, style.colorBackground, style.sgr)

/**
 * An extension for styling strings via ansi.
 *
 * @param colorForeground the color of the text
 * @param colorBackground the color of the area behind the text
 * @param sgr a list of special effects to apply to the text, such as **bold**
 */
fun String.style(
    colorForeground: AnsiColor = AnsiColor.Default,
    colorBackground: AnsiColor = AnsiColor.Default,
    sgr: AnsiSGR = AnsiSGR.Normal
): String = this.style(colorForeground, colorBackground, arrayOf(sgr))

/**
 * An extension for styling strings via ansi.
 *
 * @param colorForeground the color of the text
 * @param colorBackground the color of the area behind the text
 * @param sgr a list of special effects to apply to the text, such as **bold**
 */
fun String.style(
    colorForeground: AnsiColor = AnsiColor.Default,
    colorBackground: AnsiColor = AnsiColor.Default,
    sgr: Array<AnsiSGR>
): String {
    val startColor =
        "${CSI}${sgr.joinToString(";") { it.enable.toString() }};${colorForeground.foreground};${colorBackground.background}m"
    val endColor =
        "${CSI}${sgr.joinToString(";") { it.disable.toString() }};${AnsiColor.Default.foreground};${AnsiColor.Default.background}m"
    return startColor + this + endColor
}

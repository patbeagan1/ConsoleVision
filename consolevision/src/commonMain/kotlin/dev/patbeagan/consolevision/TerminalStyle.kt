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

fun String.style(
    colorForeground: AnsiColor = AnsiColor.Default,
    colorBackground: AnsiColor = AnsiColor.Default,
    vararg sgr: AnsiSGR
): String {
    val enableStyles: String = sgr.joinToString(";") { it.enable.toString() }
    val disableStyles: String = sgr.joinToString(";") { it.disable.toString() }
    val startColor =
        "$CSI$enableStyles;${colorForeground.foreground};${colorBackground.background}m"
    val endColor =
        "$CSI$disableStyles;${AnsiColor.Default.foreground};${AnsiColor.Default.background}m"
    return "${startColor}${this}${endColor}"
}

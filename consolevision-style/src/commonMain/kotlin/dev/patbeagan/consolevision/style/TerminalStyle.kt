package dev.patbeagan.consolevision.style

import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Special.CSI
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.SGR

/**
 * Defines the style for a single character
 */
data class TerminalStyle(
    /**
     * The foreground color of this character
     */
    val colorForeground: Color = Color.Default,
    /**
     * The background color of this character
     */
    val colorBackground: Color = Color.Default,
    /**
     * The graphics applied to this character. See [SGR] for more info.
     */
    val sgr: SGR = SGR.Normal
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
    colorForeground: Color = Color.Default,
    colorBackground: Color = Color.Default,
    sgr: SGR = SGR.Normal
): String = this.style(colorForeground, colorBackground, arrayOf(sgr))

/**
 * An extension for styling strings via ansi.
 *
 * @param colorForeground the color of the text
 * @param colorBackground the color of the area behind the text
 * @param sgr a list of special effects to apply to the text, such as **bold**
 */
fun String.style(
    colorForeground: Color = Color.Default,
    colorBackground: Color = Color.Default,
    sgr: Array<SGR>
): String {
    val startColor =
        "${CSI}${sgr.joinToString(";") { it.enable.toString() }};${colorForeground.foreground};${colorBackground.background}m"
    val endColor =
        "${CSI}${sgr.joinToString(";") { it.disable.toString() }};${Color.Default.foreground};${Color.Default.background}m"
    return startColor + this + endColor
}

package dev.patbeagan.consolevision.ansi

object StyleExtensions {
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
            "${AnsiConstants.CSI}${sgr.joinToString(";") { it.enable.toString() }};${colorForeground.foreground};${colorBackground.background}m"
        val endColor =
            "${AnsiConstants.CSI}${sgr.joinToString(";") { it.disable.toString() }};${AnsiColor.Default.foreground};${AnsiColor.Default.background}m"
        return startColor + this + endColor
    }
}

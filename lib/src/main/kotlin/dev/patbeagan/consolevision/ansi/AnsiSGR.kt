package dev.patbeagan.consolevision.ansi

/**
 * Select Graphic Rendition
 *
 * This applies visual effects to the text.
 * Most effects have 2 codes: one for turning on and one for turning off.
 * The default is SGR -> 0, which resets all effects.
 */
sealed class AnsiSGR(val enable: Int, val disable: Int? = 0) {
    object RESET : AnsiSGR(0)
    object Bold : AnsiSGR(1)
    object Dim : AnsiSGR(2)
    object Italic : AnsiSGR(3)
    object Underline : AnsiSGR(4)
    object Blink : AnsiSGR(5, 25)
    object Concealed : AnsiSGR(8, 28)
    object StrikeThrough : AnsiSGR(9, 29)
    object UnderlineDoubled : AnsiSGR(21)
    object Normal : AnsiSGR(22)
    object Framed : AnsiSGR(51, 54)
    object Encircled : AnsiSGR(52, 54)
    object Superscript : AnsiSGR(73)
    object Subscript : AnsiSGR(74)

    fun enableString() = "${AnsiConstants.CSI}${this.enable}m"
    fun disableString() = "${AnsiConstants.CSI}${this.disable}m"
}
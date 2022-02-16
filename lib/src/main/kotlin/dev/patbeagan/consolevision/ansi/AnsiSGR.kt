package dev.patbeagan.consolevision.ansi

/**
 * Select Graphic Rendition
 *
 * This applies visual effects to the text.
 * Most effects have 2 codes: one for turning on and one for turning off.
 * The default is SGR -> 0, which resets all effects.
 */
enum class AnsiSGR(
    val enable: Int,
    val disable: Int? = 0
) {
    RESET(0),
    Bold(1),
    Dim(2),
    Italic(3),
    Underline(4),
    Blink(5, 25),
    Concealed(8, 28),
    StrikeThrough(9, 29),
    UnderlineDoubled(21),
    Normal(22),
    Framed(51, 54),
    Encircled(52, 54),
    Superscript(73),
    Subscript(74);

    fun enableString() = "${AnsiConstants.CSI}${this.enable}m"
    fun disableString() = "${AnsiConstants.CSI}${this.disable}m"
}

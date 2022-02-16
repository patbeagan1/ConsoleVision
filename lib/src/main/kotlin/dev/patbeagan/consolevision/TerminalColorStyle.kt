package dev.patbeagan.consolevision

import kotlin.random.Random

object TerminalColorStyle {
    /**
     * Terminal escape sequence
     */
    const val ESC = "\u001B"

    /**
     * Control Sequence Introducer
     */
    const val CSI = "$ESC["

    /**
     * Reset Initial State
     */
    const val RIS = "${ESC}c"

    const val HIDE_CURSOR = "$CSI?25l"

    const val CURSOR_TO_START = "${CSI}1;1H"

    const val DOTS_LOW = '░'
    const val DOTS_MED = '▒'
    const val DOTS_HIGH = '▓'

    fun getCursorToPosition(x: Int, y: Int): String = "$CSI$y;${x}H"

    sealed class Colors(val foreground: String, val background: String) {
        object Default : Colors("39", "49")
        object White : Colors("30", "40")
        object Black2 : Colors("37", "47")
        object Black1 : Colors("90", "100")
        object Black : Colors("97", "107")
        object Blue : Colors("34", "44")
        object BlueBright : Colors("94", "104")
        object Cyan : Colors("36", "46")
        object CyanBright : Colors("96", "106")
        object Green : Colors("32", "42")
        object GreenBright : Colors("92", "102")
        object Magenta : Colors("35", "45")
        object MagentaBright : Colors("95", "105")
        object Red : Colors("31", "41")
        object RedBright : Colors("91", "101")
        object Yellow : Colors("33", "43")
        object YellowBright : Colors("93", "103")
        class Custom(
            private val r: Int = 0,
            private val g: Int = 0,
            private val b: Int = 0
        ) : Colors("38;2;$r;$g;$b", "48;2;$r;$g;$b") {

            constructor(argb: ARGB) : this(argb.r, argb.g, argb.b)

            fun mutate(variation: Int): Colors {
                fun Int.newColorVal() = this + (Random.nextInt() % variation).coerceIn(0..255)
                return Custom(r.newColorVal(), g.newColorVal(), b.newColorVal())
            }
        }

        class CustomPreset(value: Int = 0) : Colors("38;5;$value", "48;5;$value")
    }

    // Select Graphic Rendition
    sealed class SGR(val enable: Int, val disable: Int? = 0) {
        object RESET : SGR(0)
        object Bold : SGR(1)
        object Dim : SGR(2)
        object Italic : SGR(3)
        object Underline : SGR(4)
        object Blink : SGR(5, 25)
        object Concealed : SGR(8, 28)
        object StrikeThrough : SGR(9, 29)
        object UnderlineDoubled : SGR(21)
        object Normal : SGR(22)
        object Framed : SGR(51, 54)
        object Encircled : SGR(52, 54)
        object Superscript : SGR(73)
        object Subscript : SGR(74)

        fun enableString() = "$CSI${this.enable}m"
        fun disableString() = "$CSI${this.disable}m"
    }

    data class ARGB(val a: Int, val r: Int, val g: Int, val b: Int) {
        fun argbToColorInt(withAlpha: Boolean = true): Int =
            (a shl 24).takeIf { withAlpha } ?: 0
                .or(r shl 16)
                .or(g shl 8)
                .or(b)
    }

    fun String.style(
        colorForeground: Colors = Colors.Default,
        colorBackground: Colors = Colors.Default,
        sgr: SGR = SGR.Normal
    ): String = this.style(colorForeground, colorBackground, arrayOf(sgr))

    fun String.style(
        style: TerminalStyle
    ): String = this.style(style.colorForeground, style.colorBackground, style.sgr)

    fun String.style(
        colorForeground: Colors = Colors.Default,
        colorBackground: Colors = Colors.Default,
        sgr: Array<SGR>
    ): String {
        val startColor =
            "$CSI${sgr.joinToString(";") { it.enable.toString() }};${colorForeground.foreground};${colorBackground.background}m"
        val endColor =
            "$CSI${sgr.joinToString(";") { it.disable.toString() }};${Colors.Default.foreground};${Colors.Default.background}m"
        return startColor + this + endColor
    }

    data class TerminalStyle(
        val colorForeground: Colors = Colors.Default,
        val colorBackground: Colors = Colors.Default,
        val sgr: SGR = SGR.Normal
    )
}

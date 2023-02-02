package dev.patbeagan.consolevision.style.ansi

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Special.CSI

/**
 * Contains relevant terminal escape sequences.
 */
object ConsoleVision {

    /**
     * Defines a color that can be displayed on the terminal.
     *
     * Foreground and background sequences are encoded differently,
     * so both are included in the color class.
     */
    sealed class Color(
        /**
         * The foreground color sequence. This controls the color of the terminal text.
         */
        val foreground: String,
        /**
         * The background color sequence. This controls the color of the background.
         */
        val background: String
    ) {
        object Default : Color("39", "49")
        object White : Color("30", "40")
        object Black2 : Color("37", "47")
        object Black1 : Color("90", "100")
        object Black : Color("97", "107")
        object Blue : Color("34", "44")
        object BlueBright : Color("94", "104")
        object Cyan : Color("36", "46")
        object CyanBright : Color("96", "106")
        object Green : Color("32", "42")
        object GreenBright : Color("92", "102")
        object Magenta : Color("35", "45")
        object MagentaBright : Color("95", "105")
        object Red : Color("31", "41")
        object RedBright : Color("91", "101")
        object Yellow : Color("33", "43")
        object YellowBright : Color("93", "103")
        class CustomPreset(value: Int = 0) : Color("38;5;$value", "48;5;$value")
        class Custom(colorInt: ColorInt) : Color(
            "38;2;${colorInt.colorRed};${colorInt.colorGreen};${colorInt.colorBlue}",
            "48;2;${colorInt.colorRed};${colorInt.colorGreen};${colorInt.colorBlue}"
        )
    }

    /**
     * Select Graphic Rendition
     *
     * This applies visual effects to the text.
     * Most effects have 2 codes: one for turning on and one for turning off.
     * The default is SGR -> 0, which resets all effects.
     */
    enum class SGR(
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

        fun enableString() = "$CSI${this.enable}m"
        fun disableString() = "$CSI${this.disable}m"
    }

    /**
     * A set of constants which are used to start ansi command sequences,
     * or apply non-text style changes.
     */
    object Special {
        /**
         * Terminal escape sequence
         *
         * This is used to start almost every other style.
         */
        const val ESC: String = "\u001B"

        /**
         * Control Sequence Introducer
         *
         * This is used to start special effects
         * and other meta commands within the terminal
         */
        const val CSI: String = "$ESC["

        /**
         * Reset Initial State
         *
         * This asks the terminal to reset to its initial state.
         * In practice, that means that the screen gets cleared and
         * the cursor returns to the top left most position.
         */
        const val RIS: String = "${ESC}c"

        /**
         * This makes the cursor stop blinking.
         */
        const val HIDE_CURSOR: String = "$CSI?25l"

        /**
         * This makes the cursor return to its starting position.
         * The (1,1) position is in the top left corner of the terminal window.
         */
        const val CURSOR_TO_START: String = "${CSI}1;1H"

        /**
         * This is a more general case of [CURSOR_TO_START]
         *
         * It allows you to choose which coordinate you would like to go to
         */
        fun cursorToPosition(x: Int, y: Int): String = "$CSI$y;${x}H"
    }
}

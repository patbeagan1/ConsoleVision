package dev.patbeagan.consolevision.ansi

/**
 * A set of constants which are used to start ansi command sequences,
 * or apply non-text style changes.
 */
object AnsiConstants {
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

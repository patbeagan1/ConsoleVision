package dev.patbeagan.consolevision.ansi

import dev.patbeagan.consolevision.types.ColorInt

sealed class AnsiColor(val foreground: String, val background: String) {
    object Default : AnsiColor("39", "49")
    object White : AnsiColor("30", "40")
    object Black2 : AnsiColor("37", "47")
    object Black1 : AnsiColor("90", "100")
    object Black : AnsiColor("97", "107")
    object Blue : AnsiColor("34", "44")
    object BlueBright : AnsiColor("94", "104")
    object Cyan : AnsiColor("36", "46")
    object CyanBright : AnsiColor("96", "106")
    object Green : AnsiColor("32", "42")
    object GreenBright : AnsiColor("92", "102")
    object Magenta : AnsiColor("35", "45")
    object MagentaBright : AnsiColor("95", "105")
    object Red : AnsiColor("31", "41")
    object RedBright : AnsiColor("91", "101")
    object Yellow : AnsiColor("33", "43")
    object YellowBright : AnsiColor("93", "103")
    class CustomPreset(value: Int = 0) : AnsiColor("38;5;$value", "48;5;$value")
    class Custom(private val colorInt: ColorInt) : AnsiColor(
        "38;2;${colorInt.colorRed};${colorInt.colorGreen};${colorInt.colorBlue}",
        "48;2;${colorInt.colorRed};${colorInt.colorGreen};${colorInt.colorBlue}"
    )
}

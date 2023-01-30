package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.ansi.AnsiConstants
import kotlin.test.Test

class MainTest {
    @Test
    fun hello() {
        println("world")
    }

    @Test
    fun movingTheCursor() {
        println(AnsiConstants.RIS)
        println(AnsiConstants.cursorToPosition(1, 5))
        "Hello".style(colorForeground = AnsiColor.Red).also { println(it) }
        println(AnsiConstants.cursorToPosition(20, 4))
        "World".style(colorForeground = AnsiColor.Blue).also { println(it) }
        println(AnsiConstants.cursorToPosition(4, 3))
        "World".style(colorForeground = AnsiColor.Green).also { println(it) }
        println(AnsiConstants.cursorToPosition(4, 2))
        "Kenobi".also { println(it) }
    }
}
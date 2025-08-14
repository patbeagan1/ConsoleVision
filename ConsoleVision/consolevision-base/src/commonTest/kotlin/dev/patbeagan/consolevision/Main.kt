package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Special
import dev.patbeagan.consolevision.style.style
import kotlin.test.Test

class MainTest {
    @Test
    fun hello() {
        println("world")
    }

    @Test
    fun movingTheCursor() {
        println(Special.RIS)
        println(Special.cursorToPosition(1, 5))
        "Hello".style(colorForeground = Color.Red).also { println(it) }
        println(Special.cursorToPosition(20, 4))
        "World".style(colorForeground = Color.Blue).also { println(it) }
        println(Special.cursorToPosition(4, 3))
        "World".style(colorForeground = Color.Green).also { println(it) }
        println(Special.cursorToPosition(4, 2))
        "Kenobi".also { println(it) }
    }
}
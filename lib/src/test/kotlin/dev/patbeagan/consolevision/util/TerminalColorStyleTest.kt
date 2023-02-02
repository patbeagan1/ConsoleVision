package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.Color256
import dev.patbeagan.consolevision.style.ansi.ConsoleVision
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color.Blue
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color.Custom
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color.CustomPreset
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color.Green
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Color.Red
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.SGR
import dev.patbeagan.consolevision.style.ansi.ConsoleVision.Special.cursorToPosition
import dev.patbeagan.consolevision.style.style
import dev.patbeagan.consolevision.types.colorDistanceFrom
import org.junit.Test

internal class TerminalColorStyleTest {
    @Test
    fun demoSGRValues() {
        SGR::class.sealedSubclasses.forEach {
            println(it.simpleName?.style(sgr = it.objectInstance ?: return@forEach))
        }
    }

    @Test
    fun demoPresetColors() {
        (0..255).forEach {
            if (it % 17 == 0) println()
            "$it".padEnd(4).style(CustomPreset(it)).also { print(it) }
        }
    }

    @Test
    fun demoInlineUsage() {
        println("Todd wanted a ${"blue".style(Blue)} car")
        val styledText = "red on green".style(
            Red,
            Green,
            SGR.Bold
        )
        println(
            "Todd ${"wanted".style(sgr = SGR.Italic)} a $styledText car"
        )
    }

    @Test
    fun demoNamedColors() {
        ConsoleVision.Color::class.sealedSubclasses.forEach {
            safeLet(it.simpleName, it.objectInstance) { name, color ->
                println(name.style(color) + "test")
            }
        }
    }

    @Test
    fun `cursor position`() {
        "Hello".style(colorForeground = Red).also { println(it) }
        println(cursorToPosition(3, 10))
        "World".style(colorForeground = Blue).also { println(it) }
    }

    @Test
    fun demoInlineSGR() {
        SGR::class.sealedSubclasses.forEach {
            safeLet(it.simpleName, it.objectInstance) { name, sgr ->
                println("test${name.style(sgr = sgr)}test")
            }
        }
        println()
        val styledText = "test bold framed".style(
            sgr = arrayOf(
                SGR.Bold,
                SGR.Framed
            )
        )
        println(
            "1test${SGR.Underline.enableString()}2test${styledText}4test"
        )
    }

    @Test
    fun testColorBlending() {
        (50..100).forEach { y ->
            (100 downTo 50).forEach { x ->
                " ".style(colorBackground = Custom(ColorInt.from(255, x, y, x))).also { print(it) }
            }
            println()
        }
    }

    @Test
    fun testGreyscaleColors() {
        (0..255).forEach {
            if (it % 16 == 0) println()
            print(
                "X".style(colorBackground = Custom(ColorInt.from(255, it)))
            )
        }
        println()
    }

    @Test
    fun testColorDistance() {
        val set = Color256.values().toSet() // .sortedBy { it.color }//.colorDistance(0) }
        val set2 = Color256.values().toSet().shuffled()
            .sortedBy { ColorInt(it.color).colorDistanceFrom(ColorInt(0)) }
        set.forEachIndexed { index, it ->
            " ".also { print(it) }
            print(
                " ".style(
                    colorBackground = CustomPreset(it.number),
                    colorForeground = CustomPreset(set2[index].number)
                )
            )
            print(
                " ".style(
                    colorForeground = CustomPreset(it.number),
                    colorBackground = CustomPreset(set2[index].number)
                )
            )
            if (index % 8 == 7) println()
        }
    }
}

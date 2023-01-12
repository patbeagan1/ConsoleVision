package dev.patbeagan.consolevision.util

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.ansi.AnsiColor.Blue
import dev.patbeagan.consolevision.ansi.AnsiColor.Custom
import dev.patbeagan.consolevision.ansi.AnsiColor.CustomPreset
import dev.patbeagan.consolevision.ansi.AnsiColor.Green
import dev.patbeagan.consolevision.ansi.AnsiColor.Red
import dev.patbeagan.consolevision.ansi.AnsiConstants.ESC
import dev.patbeagan.consolevision.ansi.AnsiSGR
import dev.patbeagan.consolevision.ansi.Color256
import dev.patbeagan.consolevision.style
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.types.colorDistanceFrom
import org.junit.Test

internal class TerminalColorStyleTest {
    @Test
    fun demoSGRValues() {
        AnsiSGR::class.sealedSubclasses.forEach {
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
        val styledText = "redOnGreen".style(
            Red,
            Green,
            AnsiSGR.Bold
        )
        println(
            "Todd ${"wanted".style(sgr = AnsiSGR.Italic)} a $styledText car"
        )
    }

    @Test
    fun demoNamedColors() {
        AnsiColor::class.sealedSubclasses.forEach {
            safeLet(it.simpleName, it.objectInstance) { name, color ->
                println(name.style(color) + "test")
            }
        }
    }

    @Test
    fun demoInlineSGR() {
        AnsiSGR::class.sealedSubclasses.forEach {
            safeLet(it.simpleName, it.objectInstance) { name, sgr ->
                println("test${name.style(sgr = sgr)}test")
            }
        }
        println()
        val styledText = "3test".style(
            sgr = arrayOf(
                AnsiSGR.Bold,
                AnsiSGR.Framed
            )
        )
        println(
            "1test${AnsiSGR.Underline.enableString()}2test${styledText}4test"
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

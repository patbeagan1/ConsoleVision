package dev.patbeagan.consolevision.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.use
import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.toList2D
import dev.patbeagan.consolevision.types.CompressedPoint
import dev.patbeagan.consolevision.types.coord
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage

class TextDrawScope {
    private val charactersMut = mutableMapOf<CompressedPoint, Triple<Char, ColorInt?, ColorInt?>>()
    val characters: Map<CompressedPoint, Triple<Char, ColorInt?, ColorInt?>> = charactersMut

    fun drawText(
        x: Int,
        y: Int,
        text: String,
        colorForeground: ColorInt? = null,
        colorBackground: ColorInt? = null
    ) {
        text.forEachIndexed { index, c ->
            val fx = x + index
            val fy = if (y % 2 == 1) y - 1 else y
            charactersMut[fx coord fy] = Triple(
                c,
                colorForeground,
                colorBackground
            )
        }
    }
}


/**
 * Creates a canvas which will render colorized output to the terminal
 * via ANSI escape codes.
 */
@Composable
fun TerminalCanvas(
    modifier: Modifier = Modifier,
    consoleVisionRuntime: ConsoleVisionRuntime,
    width: Int = 80,
    height: Int = 72,
    onRender: (String) -> Unit = { println(it) },
    content: DrawScope.(TextDrawScope) -> Unit,
) {

    val textDrawScope = TextDrawScope()

    ImageComposeScene(
        width,
        height,
    ) {
        Canvas(
            modifier.fillMaxSize()
        ) {
            content(textDrawScope)
        }
    }.use { scene ->
        scene.render()
            .use { Bitmap.makeFromImage(it) }
            .toBufferedImage()
            .toList2D()
            .let { consoleVisionRuntime.getFrame(it, textDrawScope.characters) }
            .also { onRender(it) }
    }
}
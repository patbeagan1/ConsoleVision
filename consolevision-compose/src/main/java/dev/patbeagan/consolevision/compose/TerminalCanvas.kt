package dev.patbeagan.consolevision.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.use
import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.toList2D
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage

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
    content: DrawScope.() -> Unit,
) {
    ImageComposeScene(
        width,
        height,
    ) {
        Canvas(
            modifier.fillMaxSize()
        ) {
            content()
        }
    }.use { scene ->
        scene.render()
            .use { Bitmap.makeFromImage(it) }
            .toBufferedImage()
            .toList2D()
            .let { consoleVisionRuntime.printFrame(it) }
            .also { onRender(it) }
    }
}
package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.server.RouteHandler
import io.ktor.application.ApplicationCall
import io.ktor.response.respondText
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

class GetHome : RouteHandler {
    override suspend fun handle(call: ApplicationCall) {
        val url = URL(
            "https://www.freepsdbazaar.com/wp-content/uploads/2020/06/sky-replace/sky-sunset/sunset-050-freepsdbazaar.jpg"
        )
        val file: BufferedImage = ImageIO.read(url)
        val scaledImage = ImageScaler(width = 80, height = 80).scaledImage(file)
        if (scaledImage != null) {
            val printFrame = ConsoleVisionRuntime(
                paletteImage = null,
                reductionRate = 0,
                paletteReductionRate = 0,
                isCompatPalette = false,
                shouldNormalize = false,
            ).printFrame(scaledImage)

            printFrame.let { call.respondText(it) }
        } else {
            call.respondText("Could not process the image.")
        }
    }
}
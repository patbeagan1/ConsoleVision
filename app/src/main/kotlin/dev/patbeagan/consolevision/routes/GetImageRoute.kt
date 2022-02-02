package dev.patbeagan.consolevision.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.RouteHandler
import dev.patbeagan.consolevision.Const
import io.ktor.application.ApplicationCall
import io.ktor.response.respondText
import java.io.File
import javax.imageio.ImageIO

class GetImageRoute : RouteHandler {
    override suspend fun handle(call: ApplicationCall) {
        val md5 = call.parameters["imageId"]
        val imageFolder = md5?.take(2)
        val file = File("${Const.UPLOAD_DIRECTORY_NAME}/$imageFolder/$md5.png")
        val read = ImageIO.read(file)

        call.respondText(ConsoleVisionRuntime(
            paletteImage = null,
            reductionRate = 0,
            paletteReductionRate = 0,
            isCompatPalette = false,
            shouldNormalize = false,
        ).printFrame(read))
    }
}
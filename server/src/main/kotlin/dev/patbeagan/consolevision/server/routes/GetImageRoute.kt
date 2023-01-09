package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.server.RouteHandler
import dev.patbeagan.consolevision.util.Const
import io.ktor.application.*
import io.ktor.response.*
import org.koin.core.component.inject
import org.slf4j.Logger
import java.io.File
import javax.imageio.ImageIO

class GetImageRoute : RouteHandler {
    private val logger by inject<Logger>()
    override suspend fun handle(call: ApplicationCall) {
        val md5 = call.parameters["imageId"]
        logger.info(md5)
        val imageFolder = md5?.take(2)
        val file = File("${Const.UPLOAD_DIRECTORY_NAME}/$imageFolder/$md5.png")
        val read = ImageIO.read(file)

        call.respondText(
            ConsoleVisionRuntime(
                paletteImage = null,
                ConsoleVisionRuntime.Config(
                    reductionRate = 0,
                    paletteReductionRate = 0,
                    isCompatPalette = false,
                    shouldNormalize = false,
                )
            ).printFrame(read)
        )
    }
}
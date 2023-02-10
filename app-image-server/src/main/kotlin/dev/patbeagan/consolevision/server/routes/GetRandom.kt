package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.server.RouteHandler
import dev.patbeagan.consolevision.toList2D
import dev.patbeagan.consolevision.util.Const
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.core.component.inject
import org.slf4j.Logger
import java.io.File
import javax.imageio.ImageIO

class GetRandom : RouteHandler {
    val logger by inject<Logger>()
    override suspend fun handle(call: ApplicationCall) {
        val randomImage = File(Const.UPLOAD_DIRECTORY_NAME)
            .walk()
            .toList()
            .filter { it.isDirectory }
            .random()
            .walk()
            .toList()
            .filter { it.isFile }
            .random()
        val image = ImageIO.read(randomImage)
        val text = ConsoleVisionRuntime(
            paletteImage = null,
            config = ConsoleVisionRuntime.Config(
                reductionRate = 0,
                paletteReductionRate = 0,
                isCompatPalette = false,
                shouldNormalize = false,
            )
        ).getFrame(image.toList2D())
        val nameWithoutExtension = randomImage.nameWithoutExtension
        logger.info(nameWithoutExtension)
        call.respondText("$text$nameWithoutExtension\n")
    }
}
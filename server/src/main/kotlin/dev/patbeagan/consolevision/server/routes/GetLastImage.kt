package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.server.RouteHandler
import dev.patbeagan.consolevision.toList2D
import dev.patbeagan.consolevision.util.Const.LAST_IMAGE_HASH_FILE
import dev.patbeagan.consolevision.util.Const.LAST_IMAGE_NAME
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.core.component.inject
import org.slf4j.Logger
import java.io.File
import java.io.FileReader
import javax.imageio.ImageIO

class GetLastImage : RouteHandler {
    private val logger by inject<Logger>()

    // todo update to use a database call instead
    //  https://github.com/kotlin-orm/ktorm
    override suspend fun handle(call: ApplicationCall) {
        val file = File(LAST_IMAGE_NAME)
        // todo look into blocking method call
        //  does this still apply in a coroutine?
        val read = ImageIO.read(file)
        val lastUploadHash = FileReader(File(LAST_IMAGE_HASH_FILE))
            .use { it.readText() }
        logger.info("lastUploadHash: $lastUploadHash")

        call.respondText(
            ConsoleVisionRuntime(
                paletteImage = null,
                ConsoleVisionRuntime.Config(
                    reductionRate = 0,
                    paletteReductionRate = 0,
                    isCompatPalette = false,
                    shouldNormalize = false,
                )
            ).printFrame(read.toList2D())
        )
    }
}
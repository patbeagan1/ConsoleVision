package dev.patbeagan.consolevision.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.RouteHandler
import dev.patbeagan.consolevision.Const
import io.ktor.application.ApplicationCall
import io.ktor.response.respondText
import java.io.File
import javax.imageio.ImageIO

class GetLastImage() : RouteHandler {
    // todo update to use a database call instead
    //  https://github.com/kotlin-orm/ktorm
    override suspend fun handle(call: ApplicationCall) {
        val file = File("${Const.UPLOAD_DIRECTORY_NAME}/last.png")
        val read = ImageIO.read(file)

        call.respondText(ConsoleVisionRuntime(
            paletteImage = null,
            reductionRate = 0,
            paletteReductionRate = 0,
            isCompatPalette = false,
            shouldNormalize = false,
        ).printFrame(read))    }
}
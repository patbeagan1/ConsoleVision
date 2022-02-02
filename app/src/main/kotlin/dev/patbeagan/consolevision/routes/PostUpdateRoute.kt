package dev.patbeagan.consolevision.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.RouteHandler
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.Const
import dev.patbeagan.consolevision.util.getByteData
import io.ktor.application.ApplicationCall
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respondText
import io.ktor.utils.io.errors.IOException
import org.apache.commons.codec.digest.DigestUtils
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createDirectory

class PostUpdateRoute : RouteHandler {
    fun ensureDirectory(s: String) = try {
        Path(s).createDirectory()
    } catch (e: FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    } catch (e: java.nio.file.FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    }

    private fun saveProcessedImage(scaledImage: BufferedImage) {
        ensureDirectory(Const.UPLOAD_DIRECTORY_NAME)
        val md5 = DigestUtils.md5Hex(scaledImage.getByteData())
        println(md5)
        val imageFolder = md5.take(2)
        ensureDirectory("${Const.UPLOAD_DIRECTORY_NAME}/$imageFolder")
        ImageIO.write(scaledImage, "png", File("${Const.UPLOAD_DIRECTORY_NAME}/$imageFolder/$md5.png"))
        attemptToWriteLast(scaledImage)
    }

    private fun attemptToWriteLast(scaledImage: BufferedImage) {
        // needed in case multiple users access the server at the same time
        try {
            ImageIO.write(scaledImage, "png", File("${Const.UPLOAD_DIRECTORY_NAME}/last.png"))
        } catch (e: IOException) {
        } catch (e: java.io.IOException) {
        }
    }

    override suspend fun handle(call: ApplicationCall) {
        //curl -X POST -F 'image=@./Landscape.jpg' localhost:8080/upload
        val multipartData = call.receiveMultipart()
        multipartData.forEachPart { part: PartData ->
            when (part) {
                is PartData.FileItem -> {
                    println(part.headers.entries().forEach {
                        println(it.key + ":::" + it.value)
                    })
                    when (part.headers["Content-Type"]) {
                        "image/gif",
                        "image/jpeg",
                        "image/png",
                        "image/svg+xml",
                        "image/webp",
                        -> {
                            val file = ImageIO.read(ByteArrayInputStream(part.streamProvider().readBytes()))
                            val scaledImage = file?.let {
                                ImageScaler(width = 80, height = 80).scaledImage(it)
                            }?.also { saveProcessedImage(it) }

                            if (scaledImage != null) {
                                val printFrame = ConsoleVisionRuntime(
                                    paletteImage = null,
                                    reductionRate = 0,
                                    paletteReductionRate = 0,
                                    isCompatPalette = false,
                                    shouldNormalize = false,
                                ).printFrame(scaledImage)

                                printFrame
                                    .also { println(it) }
                                    .let { call.respondText("$it\n${DigestUtils.md5Hex(scaledImage.getByteData())}") }
                            } else {
                                call.respondText("Could not process the image.")
                            }
                        }
                    }
                }
                is PartData.FormItem -> TODO()
                is PartData.BinaryItem -> TODO()
            }
        }
    }
}
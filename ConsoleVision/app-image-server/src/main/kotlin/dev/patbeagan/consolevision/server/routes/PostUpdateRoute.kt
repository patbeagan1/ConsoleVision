package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.server.RouteHandler
import dev.patbeagan.consolevision.toList2D
import dev.patbeagan.consolevision.util.Const
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.errors.*
import org.apache.commons.codec.digest.DigestUtils
import org.koin.core.component.inject
import org.slf4j.Logger
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileWriter
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createDirectory

class PostUpdateRoute : RouteHandler {
    private val logger by inject<Logger>()

    private fun ensureDirectory(s: String) = try {
        Path(s).createDirectory()
    } catch (e: FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    } catch (e: java.nio.file.FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    }

    private fun saveProcessedImage(scaledImage: BufferedImage) {
        ensureDirectory(Const.UPLOAD_DIRECTORY_NAME)
        val md5 = DigestUtils.md5Hex(scaledImage.getByteData())
        logger.info(md5)
        val imageFolder = md5.take(2)
        ensureDirectory("${Const.UPLOAD_DIRECTORY_NAME}/$imageFolder")
        ImageIO.write(
            scaledImage,
            "png",
            File("${Const.UPLOAD_DIRECTORY_NAME}/$imageFolder/$md5.png")
        )
        attemptToWriteLast(scaledImage)
    }

    private fun attemptToWriteLast(scaledImage: BufferedImage) {
        // needed in case multiple users access the server at the same time
        try {
            ImageIO.write(scaledImage, "png", File(Const.LAST_IMAGE_NAME))
            FileWriter(File(Const.LAST_IMAGE_HASH_FILE)).use {
                it.write(DigestUtils.md5Hex(scaledImage.getByteData()))
            }
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
                            val file = ImageIO.read(
                                ByteArrayInputStream(
                                    part.streamProvider().readBytes()
                                )
                            )
                            val scaledImage = file?.let {
                                ImageScaler(width = 80, height = 80).scaledImage(it)
                            }?.also { saveProcessedImage(it) }

                            if (scaledImage != null) {
                                val printFrame = ConsoleVisionRuntime(
                                    paletteImage = null,
                                    config = ConsoleVisionRuntime.Config(
                                        reductionRate = 0,
                                        paletteReductionRate = 0,
                                        isCompatPalette = false,
                                        shouldNormalize = false,
                                    )
                                ).getFrame(scaledImage.toList2D())

                                printFrame.let {
                                    val md5Hex = DigestUtils.md5Hex(scaledImage.getByteData())
                                    call.respondText("$it\n$md5Hex\n")
                                }
                            } else {
                                call.respondText("Could not process the image.")
                            }
                        }
                    }
                }

                is PartData.FormItem -> TODO()
                is PartData.BinaryItem -> TODO()
                else -> {}
            }
        }
    }
}

fun BufferedImage.getByteData(): ByteArray {
    val buffer = raster.dataBuffer as DataBufferByte
    return buffer.data
}
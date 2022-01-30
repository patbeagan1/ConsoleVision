package dev.patbeagan.consolevision

import io.ktor.application.call
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createDirectory

object Router {
    private const val TEMP_DIRECTORY_PATHNAME = "./tmp"
    private const val TEMP_FILENAME = "tmp.png"

    fun Routing.getHome() {
        //curl -X POST -F 'image=@/home/user/Pictures/wallpaper.jpg' http://example.com/upload
        get("/") {
            val url =
                URL("https://www.freepsdbazaar.com/wp-content/uploads/2020/06/sky-replace/sky-sunset/sunset-050-freepsdbazaar.jpg")
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

                printFrame
                    .also { println(it) }
                    .let { call.respondText(it) }
            } else {
                call.respondText("Could not process the image.")
            }
        }
    }

    fun Routing.postUpload() {
        //            curl -X POST -F 'image=@./Landscape.jpg' localhost:8080/upload
        post("/upload") {
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
                                val file = readPng(part.streamProvider().readBytes())
                                val scaledImage = file?.let {
                                    ImageScaler(width = 80, height = 80).scaledImage(it)
                                }

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
                                        .let { call.respondText(it) }
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

    private fun ensureTempDirectory() = try {
        Path(TEMP_DIRECTORY_PATHNAME).createDirectory()
    } catch (e: FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    } catch (e: java.nio.file.FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    }

    private fun readPng(
        bytes: ByteArray,
    ): BufferedImage? {
        ensureTempDirectory()
        val file = File(TEMP_FILENAME).also { it.writeBytes(bytes) }
        val img = ImageIO.read(file)
        file.delete()
        return img
    }
}
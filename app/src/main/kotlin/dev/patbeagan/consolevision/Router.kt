package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.util.getByteData
import io.ktor.application.call
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import org.apache.commons.codec.digest.DigestUtils
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createDirectory

object Router {
    private const val UPLOAD_DIRECTORY_NAME = "./consolevideo-uploads"

    fun Routing.getHome() {
        get("/") {
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

                printFrame
                    .also { println(it) }
                    .let { call.respondText(it) }
            } else {
                call.respondText("Could not process the image.")
            }
        }
    }

    fun Routing.getImage() {
        get("/im/{imageId}") {
            val md5 = call.parameters["imageId"]
            val imageFolder = md5?.take(2)
            val file = File("$UPLOAD_DIRECTORY_NAME/$imageFolder/$md5.png")
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

    fun Routing.postUpload() {
        //curl -X POST -F 'image=@./Landscape.jpg' localhost:8080/upload
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
                                val file = ImageIO.read(ByteArrayInputStream(part.streamProvider().readBytes()))
                                val scaledImage = file?.let {
                                    ImageScaler(width = 80, height = 80).scaledImage(it)
                                }?.also {
                                    saveProcessedImage(it)
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

    private fun saveProcessedImage(scaledImage: BufferedImage) {
        ensureDirectory(UPLOAD_DIRECTORY_NAME)
        val md5 = DigestUtils.md5Hex(scaledImage.getByteData())
        println(md5)
        val imageFolder = md5.take(2)
        ensureDirectory("$UPLOAD_DIRECTORY_NAME/$imageFolder")
        ImageIO.write(scaledImage, "png", File("$UPLOAD_DIRECTORY_NAME/$imageFolder/$md5.png"))
    }

    private fun ensureDirectory(s: String) = try {
        Path(s).createDirectory()
    } catch (e: FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    } catch (e: java.nio.file.FileAlreadyExistsException) {
        // this is ok, just making sure it is there
    }
}
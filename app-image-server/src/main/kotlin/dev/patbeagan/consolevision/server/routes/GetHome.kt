package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.server.RouteHandler
import dev.patbeagan.consolevision.util.respondContentBasedOnUserAgent
import dev.patbeagan.consolevision.util.userAgent
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import org.koin.core.component.inject
import org.slf4j.Logger

class GetHome : RouteHandler {
    private val logger by inject<Logger>()
    override suspend fun handle(call: ApplicationCall) {
        val userAgent = call.userAgent
        logger.info(userAgent)
        call.respondContentBasedOnUserAgent(
            ::renderConsoleInfo,
            ::renderSubmissionForm
        )
    }

    private suspend fun renderConsoleInfo(call: ApplicationCall) {
        call.respondText("""
                    
                    Welcome to ConsoleVision!
                    - To upload a photo, POST to the /upload endpoint. You'll receive an image hash. 
                    - To retrieve a photo, GET to the /im/{id} end point, using an image hash.
                    
                    You can add the following to your ~/.bashrc or ~/.zshrc file to add an upload command.
                    Replace localhost with the server you are accessing. 
                    
                         cvupload () {
                            curl -X POST -F 'image=@'"${'$'}1" localhost:3000/upload
                         }
                         cvimage () {
                            curl localhost:3000/im/"${'$'}1"
                         }
    
                """.replaceIndent()
        )
    }

    private suspend fun renderSubmissionForm(call: ApplicationCall) {
        call.respondHtml {
            body {
                h1 {
                    +"ConsoleVision"
                }
                h3 {
                    +"Choose a file to be converted to ANSI"
                }
                form(action = "/upload", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                    label {
                        htmlFor = "myFile"
                        +"Select a file"
                    }
                    input(type = InputType.file) {
                        id = "myFile"
                        name = "myFile"
                    }
                    br()
                    input(type = InputType.submit)
                }
                p {
                    text("By submitting an image here, you'll be uploading it to the server this form resides on. " +
                            "Once submitted, you'll receive an MD5 hash of the converted image. " +
                            "You can then use the command 'curl {servername}/im/{hash}' to see an ANSI version of the image. ")
                }
                div {
                    img(alt = "Demo image of the mona lisa",
                        src = "https://user-images.githubusercontent.com/10187351/152473836-502b5415-df40-4657-9d01-e93cf130515f.png") {
                        width = "400px"
                    }
                }
            }
        }
    }
}

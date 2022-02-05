package dev.patbeagan.consolevision.server.routes

import dev.patbeagan.consolevision.server.RouteHandler
import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label

class GetHome : RouteHandler {
    override suspend fun handle(call: ApplicationCall) {
        call.respondHtml {
            body {
                h1 {
                    +"show file select"
                }
                h3 {
                    +"Show a file-select field which allows only one file to be chosen:"
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
                    br { }
                    input(type = InputType.submit)
                }
            }
        }
    }
}

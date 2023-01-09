package dev.patbeagan.consolevision.util

import io.ktor.application.ApplicationCall

val ApplicationCall.userAgent get() = request.headers["User-Agent"]

suspend fun ApplicationCall.respondContentBasedOnUserAgent(
    renderConsole: suspend (ApplicationCall) -> Unit,
    renderHTML: suspend (ApplicationCall) -> Unit,
) {
    val userAgent = userAgent
    when {
        null == userAgent || "curl/" in userAgent -> renderConsole(this)
        else -> renderHTML(this)
    }
}

package dev.patbeagan.consolevision

import io.ktor.application.ApplicationCall

interface RouteHandler {
    suspend fun handle(call: ApplicationCall)
}
package dev.patbeagan.consolevision.server

import io.ktor.server.application.*
import org.koin.core.component.KoinComponent

interface RouteHandler : KoinComponent {
    suspend fun handle(call: ApplicationCall)
}
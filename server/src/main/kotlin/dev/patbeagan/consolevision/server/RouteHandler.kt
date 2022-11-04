package dev.patbeagan.consolevision.server

import io.ktor.application.ApplicationCall
import org.koin.core.component.KoinComponent

interface RouteHandler : KoinComponent {
    suspend fun handle(call: ApplicationCall)
}
package dev.patbeagan.consolevision.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.slf4j.event.Level


class Server : KoinComponent {
    fun startServer() {
        embeddedServer(Netty, port = 3000) {
            startKoin {
                modules(
                    Injection.dispatcherModule(),
                    Injection.loggingModule(log),
                    Router.moduleRoutes
                )
            }

            install(CallLogging) {
                level = Level.INFO
            }
            val router by inject<Router>()
            routing {
                get("/") { router.getHome.handle(call) }
                post("/upload") { router.postUpdateRoute.handle(call) }
                get("/im/{imageId}") { router.imageRoute.handle(call) }
                get("/last") { router.getLastImage.handle(call) }
                get("/random") { router.getRandom.handle(call) }
            }
        }.start(wait = true)
    }
}


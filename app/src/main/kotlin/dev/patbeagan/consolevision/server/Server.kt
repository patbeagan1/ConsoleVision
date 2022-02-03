package dev.patbeagan.consolevision.server

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.event.Level

class Server : KoinComponent {
    fun startServer() {
        embeddedServer(Netty, port = 3000) {
            startKoin {
                modules(
                    module {
                        single { log }
                    },
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
            }
        }.start(wait = true)
    }
}


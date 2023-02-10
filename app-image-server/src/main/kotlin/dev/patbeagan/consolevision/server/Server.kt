package dev.patbeagan.consolevision.server


import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class Server : KoinComponent {
    fun startServer() {
        embeddedServer(Netty, port = 3033) {
            startKoin {
                modules(
                    Injection.dispatcherModule(),
                    Injection.loggingModule(log),
                    Router.moduleRoutes
                )
            }

//            install(CallLogging) {
//                level = Level.INFO
//            }
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


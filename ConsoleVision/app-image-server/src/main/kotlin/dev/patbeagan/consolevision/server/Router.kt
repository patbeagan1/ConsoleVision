package dev.patbeagan.consolevision.server

import dev.patbeagan.consolevision.server.routes.GetHome
import dev.patbeagan.consolevision.server.routes.GetImageRoute
import dev.patbeagan.consolevision.server.routes.GetLastImage
import dev.patbeagan.consolevision.server.routes.GetRandom
import dev.patbeagan.consolevision.server.routes.PostUpdateRoute
import org.koin.dsl.module

class Router(
    val imageRoute: GetImageRoute,
    val postUpdateRoute: PostUpdateRoute,
    val getHome: GetHome,
    val getLastImage: GetLastImage,
    val getRandom: GetRandom,
) {
    companion object {
        val moduleRoutes = module {
            single { GetImageRoute() }
            single { PostUpdateRoute() }
            single { GetHome() }
            single { GetLastImage() }
            single { GetRandom() }
            single { Router(get(), get(), get(), get(), get()) }
        }
    }
}
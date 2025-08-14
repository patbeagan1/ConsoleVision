package dev.patbeagan.consolevision.server

import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.slf4j.Logger

object Injection {
    internal fun dispatcherModule(): Module = module {
        single(named("IODispatcher")) {
            Dispatchers.IO
        }
    }

    internal fun loggingModule(log: Logger): Module = module {
        single { log }
    }
}
package dev.patbeagan.consolevision.util

class Memoize1<in T, out R>(private val f: (T) -> R) : (T) -> R {
    private val values = mutableMapOf<T, R>()

    override fun invoke(x: T): R {
        if (values.size > SIZE_LIMIT) {
            val first = values.entries.first()
            values.remove(first.key)
        }
        return values.getOrPut(x) { f(x) }
    }

    companion object {
        private const val SIZE_LIMIT = 500
    }
}

class Memoize2<in S, in T, out R>(private val f: (S, T) -> R) : (S, T) -> R {
    private val values = mutableMapOf<Pair<S, T>, R>()
    private val cache = Array<Pair<S, T>?>(SIZE_LIMIT) { null }
    private var counter = 0

    override fun invoke(p1: S, p2: T): R {
        if (counter > cache.size) {
            values.remove(cache[counter % cache.size])
        }
        val pair = p1 to p2
        cache[counter++] = pair
        counter %= cache.size
        return values.getOrPut(pair) { f(p1, p2) }
    }

    companion object {
        private const val SIZE_LIMIT: Int = 500
    }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)
fun <S, T, R> ((S, T) -> R).memoize(): (S, T) -> R = Memoize2(this)

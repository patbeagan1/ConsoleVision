package com.pbeagan.demo

class Memoize1<in T, out R>(val f: (T) -> R) : (T) -> R {
    private val values = mutableMapOf<T, R>()
    override fun invoke(x: T): R {
        return values.getOrPut(x, { f(x) })
    }
}

class Memoize2<in S, in T, out R>(val f: (S, T) -> R) : (S, T) -> R {
    private val values = mutableMapOf<Pair<S, T>, R>()

    override fun invoke(p1: S, p2: T): R {
        return values.getOrPut(p1 to p2, { f(p1, p2) })
    }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize1(this)
fun <S, T, R> ((S, T) -> R).memoize(): (S, T) -> R = Memoize2(this)
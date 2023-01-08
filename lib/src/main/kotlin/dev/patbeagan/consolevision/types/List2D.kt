package dev.patbeagan.consolevision.types

@JvmInline
value class List2D<T>(private val value: MutableList<MutableList<T>>) : Iterable<T> {

    val height get() = value.size
    val width get() = value.firstOrNull()?.size ?: 0

    fun at(x: Int, y: Int): T = value[y][x]
    fun assign(x: Int, y: Int, item: T) {
        value[y][x] = item
    }

    override fun iterator(): Iterator<T> = iterator {
        this@List2D.traverseInternal(value, {}) { _, _, t -> yield(t) }
    }

    fun traverseMutate(
        onElement: (x: Int, y: Int, each: T) -> T,
    ): Unit = value.forEachIndexed { y, row ->
        row.forEachIndexed { x, t -> value[y][x] = onElement(x, y, t) }
    }

    fun <R> traverseMapIndexed(
        onElement: (x: Int, y: Int, T) -> R,
    ): List2D<R> = from(
        value.mapIndexed { y, r ->
            r.mapIndexed { x, it -> onElement(x, y, it) }
        })

    fun traverseAssign(list: List<CompressedPoint>, t: T) {
        list.forEach {
            if (it.y in this.value.indices && it.x in this.value[0].indices) {
                this.value[it.y][it.x] = t
            }
        }
    }

    fun <R> traverseMap(
        onElement: (T) -> R,
    ): List2D<R> = traverseMapIndexed { _, _, t -> onElement(t) }

    fun traverse(
        onRowEnd: () -> Unit = {},
        onElement: (x: Int, y: Int, T) -> Unit,
    ): Unit = traverseInternal(value, onRowEnd, onElement)

    private inline fun traverseInternal(
        list: MutableList<MutableList<T>>,
        onRowEnd: () -> Unit = {},
        onElement: (x: Int, y: Int, T) -> Unit,
    ): Unit = list.forEachIndexed { y, r ->
        r.forEachIndexed { x, t -> onElement(x, y, t) }
        onRowEnd()
    }

    fun isValidCoordinate(c: CompressedPoint): Boolean =
        value.isNotEmpty() &&
            value.all { it.size == value[0].size } &&
            c.y in 0..value.size &&
            c.x in 0..value[0].size

    fun printAll(delimiter: String = "\t") {
        traverse({ println() }) { _, _, t -> print("$t$delimiter") }
    }

    inline fun <reified S, reified R> mergeWith(
        other: List2D<S>,
        default: R,
        crossinline onElement: (first: T, second: S) -> R,
    ): List2D<R> = this.traverseMapIndexed { x, y, t ->
        if (other.isValidCoordinate(x coord y)) {
            onElement(this.at(x, y), other.at(x, y))
        } else {
            default
        }
    }

    fun flatten(): List<T> {
        val ret = mutableListOf<T>()
        iterator().forEach { ret.add(it) }
        return ret
    }

    companion object {
        fun <T> from(value: List<List<T>>) =
            List2D(value.map { it.toMutableList() }.toMutableList())
    }
}


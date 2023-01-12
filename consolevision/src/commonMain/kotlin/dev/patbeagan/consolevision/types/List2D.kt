package dev.patbeagan.consolevision.types

import dev.patbeagan.consolevision.types.annotation.WillInline

/**
 * This is a representation of a 2D grid.
 *
 * Common operations on 2D arrays can be done within this class.
 */
@WillInline
class List2D<T> private constructor(private val value: MutableList<MutableList<T>>) :
    Iterable<T> {

    /**
     * The height of the 2d list. This is the number of rows that it contains.
     */
    val height: Int get() = value.size

    /**
     * The width of the 2d list. This is the number of columns that it contains.
     */
    val width: Int get() = value.firstOrNull()?.size ?: 0

    /**
     * Gets the value at a certain X,Y coordinate.
     */
    fun at(x: Int, y: Int): T = value[y][x]

    /**
     * Sets the value at a certain X,Y coordinate.
     */
    fun assign(x: Int, y: Int, item: T) {
        value[y][x] = item
    }

    override fun iterator(): Iterator<T> = iterator {
        this@List2D.traverseInternal(value, {}) { _, _, t -> yield(t) }
    }

    /**
     * Traverses the 2D array, and mutates each cell.
     * This is more performant than using [map].
     */
    fun traverseMutate(
        onElement: (x: Int, y: Int, each: T) -> T,
    ): Unit = value.forEachIndexed { y, row ->
        row.forEachIndexed { x, t -> value[y][x] = onElement(x, y, t) }
    }

    /**
     * Analogous to [List.mapIndexed]
     *
     * Returns a [List2D] containing the results
     * of applying the given transform function to each element
     * and its index in the original collection.
     */
    fun <R> traverseMapIndexed(
        onElement: (x: Int, y: Int, T) -> R,
    ): List2D<R> = from(value.mapIndexed { y, r -> r.mapIndexed { x, it -> onElement(x, y, it) } })


    /**
     * Puts a given value into all the cells within a given list of coordinates.
     */
    fun traverseAssign(list: List<CompressedPoint>, t: T) {
        list.forEach {
            if (it.y in this.value.indices && it.x in this.value[0].indices) {
                this.value[it.y][it.x] = t
            }
        }
    }

    /**
     * Analogous to [List.map]
     *
     * Returns a [List2D] containing the results
     * of applying the given transform function to each element
     * in the original collection.
     */
    fun <R> traverseMap(
        onElement: (T) -> R,
    ): List2D<R> = traverseMapIndexed { _, _, t -> onElement(t) }

    /**
     * Analogous to [List.forEach]
     *
     * Traverses the [List2D], by visiting each cell.
     * Includes 2 callable blocks, for visiting an element,
     * and for when the current row is completed.
     */
    fun traverse(
        onRowEnd: () -> Unit = {},
        onElement: (x: Int, y: Int, T) -> Unit,
    ): Unit = traverseInternal(value, onRowEnd, onElement)

    private inline fun traverseInternal(
        // needed so that we can access the value field, in traverse
        list: MutableList<MutableList<T>>,
        onRowEnd: () -> Unit = {},
        onElement: (x: Int, y: Int, T) -> Unit,
    ): Unit = list.forEachIndexed { y, r ->
        r.forEachIndexed { x, t -> onElement(x, y, t) }
        onRowEnd()
    }

    /**
     * Verifies that a given point is contained in the [List2D]
     */
    fun isValidCoordinate(c: CompressedPoint): Boolean =
        value.isNotEmpty() &&
            value.all { it.size == value[0].size } &&
            c.y in 0..value.size &&
            c.x in 0..value[0].size

    /**
     * A debug method that prints all of the values that are currently in the [List2D]
     */
    fun printAll(delimiter: String = "\t") {
        traverse({ println() }) { _, _, t -> print("$t$delimiter") }
    }

    /**
     * Merges two [List2D] together.
     *
     * This is analogous to [List.zip]
     */
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

    companion object {
        /**
         * Creates a [List2D] from a List of Lists.
         */
        fun <T> from(value: List<List<T>>): List2D<T> =
            List2D(value.map { it.toMutableList() }.toMutableList())

        /**
         * Creates a [List2D] filled with a default value.
         */
        fun <T> of(width: Int, height: Int, default: T): List2D<T> =
            from((0 until height).map {
                (0 until width).map { default }
            })
    }
}


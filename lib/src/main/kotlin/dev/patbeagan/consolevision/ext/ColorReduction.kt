package dev.patbeagan.consolevision.ext

import dev.patbeagan.consolevision.ansi.Color256
import dev.patbeagan.consolevision.util.distance
import kotlin.math.abs

/**
 * Takes any color, and finds the closest matching color from the 256 color set.
 *
 * @return the closest matching [Color256]
 */
fun reduceColor(color: Int) = Color256.values().associateBy {
    abs(color - it.color)
}.toSortedMap()[0]
    ?.number ?: 0

/**
 * Takes any color, and finds the closest matching color from
 *
 * 16 evenly dispersed colors in the 256 color set.
 *
 * @return one of 16 possible [Color256]
 */
fun reduceColor16(color: Int): Int {
    val toSortedMap: Map.Entry<Double, Color256>? =
        Color256.values().take(16).associateBy { color256 ->
            distance(
                color shr 16 and 255,
                color shr 8 and 255,
                color and 255,
                color256.color shr 16 and 255,
                color256.color shr 8 and 255,
                color256.color and 255
            )
            //            abs(color - color256.color)
        }.minByOrNull { it.key }
    return toSortedMap?.value?.number ?: 0
}

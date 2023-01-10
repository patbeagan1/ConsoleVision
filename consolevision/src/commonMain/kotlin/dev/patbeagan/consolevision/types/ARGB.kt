package dev.patbeagan.consolevision.types

@Deprecated("Please use ColorInt instead, if possible")
data class ARGB(
    val a: Int,
    val r: Int,
    val g: Int,
    val b: Int
)

fun Int.colorIntToARGB(): ARGB = ARGB(
    this shr 24 and 255,
    this shr 16 and 255,
    this shr 8 and 255,
    this and 255
)

fun ARGB.argbToColorInt(withAlpha: Boolean = true): Int =
    (a shl 24).takeIf { withAlpha } ?: 0
        .or(r shl 16)
        .or(g shl 8)
        .or(b)


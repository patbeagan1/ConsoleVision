package dev.patbeagan.consolevision

enum class CompressionStyle {
    UP_DOWN, DOTS;

    val symbol
        get() = when (this) {
            UP_DOWN -> "▄"
            DOTS -> "▓"
        }
}
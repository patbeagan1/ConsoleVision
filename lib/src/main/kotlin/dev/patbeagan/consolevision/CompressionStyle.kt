package dev.patbeagan.consolevision

enum class CompressionStyle {
    UP_DOWN, DOTS_HIGH, DOTS_MED, DOTS_LOW;

    val symbol: String
        get() = when (this) {
            UP_DOWN -> LOWER_HALF
            DOTS_HIGH -> DOTS_HIGH_CHAR
            DOTS_MED -> DOTS_MED_CHAR
            DOTS_LOW -> DOTS_LOW_CHAR
        }.toString()

    private companion object {
        const val LOWER_HALF = '▄'
        const val DOTS_LOW_CHAR = '░'
        const val DOTS_MED_CHAR = '▒'
        const val DOTS_HIGH_CHAR = '▓'
    }
}

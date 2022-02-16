package dev.patbeagan.consolevision.types

/**
 * Allows us to choose which characters will be overlaid on
 * each other to make the image show up.
 *
 * The default is to make the foreground be the lower half of the
 */
enum class CompressionStyle {
    UPPER_HALF,
    LOWER_HALF,
    DOTS_HIGH,
    DOTS_MED,
    DOTS_LOW;

    val symbol: String
        get() = when (this) {
            UPPER_HALF -> UPPER_HALF_CHAR
            LOWER_HALF -> LOWER_HALF_CHAR
            DOTS_HIGH -> DOTS_HIGH_CHAR
            DOTS_MED -> DOTS_MED_CHAR
            DOTS_LOW -> DOTS_LOW_CHAR
        }.toString()

    private companion object {
        const val UPPER_HALF_CHAR = '▀'
        const val LOWER_HALF_CHAR = '▄'
        const val DOTS_LOW_CHAR = '░'
        const val DOTS_MED_CHAR = '▒'
        const val DOTS_HIGH_CHAR = '▓'
    }
}

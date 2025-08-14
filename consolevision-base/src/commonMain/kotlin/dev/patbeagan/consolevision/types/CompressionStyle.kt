package dev.patbeagan.consolevision.types

/**
 * Allows us to choose which characters will be overlaid on
 * each other to make the image show up.
 *
 * The default is to make the foreground be the lower half of the
 */
enum class CompressionStyle(val symbol: Char) {
    UPPER_HALF('▀'),
    LOWER_HALF('▄'),
    DOTS_HIGH('░'),
    DOTS_MED('▒'),
    DOTS_LOW('▓');
}

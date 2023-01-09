package dev.patbeagan.consolevision.imagefilter

import java.awt.image.BufferedImage

interface ImageFilter {
    operator fun invoke(bufferedImage: BufferedImage)
}

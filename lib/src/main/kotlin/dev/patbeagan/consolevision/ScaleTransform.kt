package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.util.getScaleToBoundBy
import java.awt.image.BufferedImage

class ScaleTransform(image: BufferedImage, width: Int?, height: Int?) {
    val scaleTransform by lazy {
        image.getScaleToBoundBy(width, height)
    }
}
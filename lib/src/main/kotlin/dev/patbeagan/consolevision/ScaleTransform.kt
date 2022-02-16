package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.util.getScaleToBoundBy
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

class ScaleTransform(image: BufferedImage, width: Int?, height: Int?) {

    val scaleTransform: Pair<Double, AffineTransformOp> by lazy {
        image.getScaleToBoundBy(width, height)
    }
}

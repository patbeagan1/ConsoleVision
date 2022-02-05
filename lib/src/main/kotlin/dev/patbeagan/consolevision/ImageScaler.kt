package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.util.scale
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

class ImageScaler(private val width: Int?, private val height: Int?) {
    private var transform: ScaleTransform? = null

    fun scaledImage(file: BufferedImage): BufferedImage? {
        val scaleTransform = getImageTransform(file) ?: return null
        return file.scale(
            scaleTransform.first,
            scaleTransform.second
        )
    }

    private fun getImageTransform(bufferedImage: BufferedImage?): Pair<Double, AffineTransformOp>? =
        bufferedImage?.let { image ->
            val scaleTransform = transform ?: ScaleTransform(image, width, height).also {
                transform = it
            }
            scaleTransform.scaleTransform
        }
}

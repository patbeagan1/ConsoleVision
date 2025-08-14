package dev.patbeagan.consolevision

import java.awt.geom.AffineTransform
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

    private class ScaleTransform(image: BufferedImage, width: Int?, height: Int?) {
        val scaleTransform: Pair<Double, AffineTransformOp> by lazy {
            image.getScaleToBoundBy(width, height)
        }
    }
}

// todo make this private
fun BufferedImage.scale(
    scale: Double,
    affineTransformOp: AffineTransformOp
): BufferedImage {
    val w = width
    val h = height
    val w2 = (w * scale).toInt()
    val h2 = (h * scale).toInt()
    val after = BufferedImage(w2, h2, type)
    affineTransformOp.filter(this, after)
    return after
}

// todo make this private
fun BufferedImage.getScaleToBoundBy(w: Int?, h: Int?): Pair<Double, AffineTransformOp> {
    val scale = when {
        w == null && h == null -> minOf(90.0 / width, 90.0 / height)
        w != null && h != null -> minOf(w.toDouble() / width, h.toDouble() / height)
        w != null -> w.toDouble() / width
        h != null -> h.toDouble() / height
        else -> throw Exception("Can't scale by this amount: ${w to h}")
    }
    return scale to AffineTransformOp(
        AffineTransform.getScaleInstance(scale, scale),
        AffineTransformOp.TYPE_BICUBIC
    )
}

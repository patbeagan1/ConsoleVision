package dev.patbeagan.app.demo.compose

data class AnimationLimiter(
    val min: Float,
    val max: Float,
    val duration: Int,
    val fps: Float
) {
    private val bucketer = Bucketer(
        (min * Scale).toInt(),
        (max * Scale).toInt(),
        ((1000 / fps) * Scale).toInt()
    )

    fun toBucketValue(v: Float) = bucketer.toBucketValue(v.toInt())

    companion object {
        private const val Scale = 1000
    }
}

interface IBucketer {
    fun toBucketValue(v: Int): Int?
}

data class Bucketer(
    val min: Int,
    val max: Int,
    val bucketSize: Int
) : IBucketer {
    private val bucketValues = (min..max step bucketSize).toList()

    override fun toBucketValue(v: Int) = bucketValues.asReversed().firstOrNull { v > it }
}

fun main() {
//    val a = AnimationLimiter(3f, 70f, 6, 10f)
    val a = AnimationLimiter(
        min = 0.5f,
        max = 0.8f,
        duration = 500,
        fps = 30f
    )

    (0..100).forEach {
        println("$it ${a.toBucketValue(it.toFloat())}")
    }

    val b = Bucketer(0, 100, 5)

    (0..100).forEach {
        println("$it ${b.toBucketValue(it)}")
    }
}
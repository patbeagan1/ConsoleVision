package dev.patbeagan.consolevision

import kotlin.math.pow

@JvmInline
value class CompressedPoint(private val base: Long) {
    private val x get() = (base shr 32).toInt()
    private val y get() = base.toInt() // (base and (2.0.pow(32) - 1).toLong()).toInt()

    override fun toString(): String =
        "CompressedPoint of ${this.x} to ${this.y}\n" +
            "                   ${Integer.toBinaryString(x)}\n" +
            "                   ${Integer.toBinaryString(y)}"

    companion object {
        fun from(x: Int, y: Int) = CompressedPoint(
            // toLong modifies the leading bit to keep the sign, so we have to do it manually
            (x.toLong() shl 32) or (y.toLong() and (2.0.pow(32) - 1).toLong())
        )
    }
}

fun main() {
    CompressedPoint(1L).let { println(it) }
    CompressedPoint.from(1, 2).let { println(it) }
    CompressedPoint.from(1728372628, 373828738).let { println(it) }
    println(Int.MAX_VALUE)
    CompressedPoint.from(Int.MAX_VALUE, Int.MAX_VALUE).let { println(it) }
    println(Int.MIN_VALUE)
    CompressedPoint.from(Int.MIN_VALUE, Int.MIN_VALUE).let { println(it) }
    CompressedPoint.from(-100, Int.MIN_VALUE).let { println(it) }
    CompressedPoint.from(-2, 10).let { println(it) }
    CompressedPoint.from(Int.MAX_VALUE, Int.MIN_VALUE).let { println(it) }
    CompressedPoint.from(-10, -10).let { println(it) }
}

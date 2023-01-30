package dev.patbeagan.consolevision.types

data class CoordRect(
    val lesser: CompressedPoint,
    val greater: CompressedPoint
) {
    fun modifyBy(
        lx: Int = 0,
        ly: Int = 0,
        gx: Int = 0,
        gy: Int = 0
    ) = CoordRect(
        (this.lesser.x + lx) coord (this.lesser.y + ly),
        (this.greater.x + gx) coord (this.greater.y + gy)
    )
}
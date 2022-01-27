package dev.patbeagan.consolevision.util

val Int.colorAlpha: Int get() = this shr 24 and 255
val Int.colorRed get() = this shr 16 and 255
val Int.colorGreen get() = this shr 8 and 255
val Int.colorBlue get() = this and 255

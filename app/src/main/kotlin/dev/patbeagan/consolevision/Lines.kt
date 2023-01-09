package dev.patbeagan.consolevision.consolevision.demo

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.ansi.AnsiConstants.HIDE_CURSOR

import dev.patbeagan.consolevision.ansi.AnsiConstants.RIS
import dev.patbeagan.consolevision.style
import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.types.CompressedPoint
import dev.patbeagan.consolevision.types.CoordRect
import dev.patbeagan.consolevision.types.List2D
import dev.patbeagan.consolevision.types.coord
import dev.patbeagan.consolevision.types.coordRect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

fun main() {
    (1 coord 1).lineByDDATo(3 coord 4)
    getCircleByBresenham(1 coord 1, 3)
}

fun CompressedPoint.lineByDDATo(end: CompressedPoint): List<CompressedPoint> {
    val dx = end.x - x
    val dy = end.y - y
    val steps = max(abs(dx), abs(dy))
    val stepX = dx / steps.toFloat();
    val stepY = dy / steps.toFloat();

    var x = x.toFloat()
    var y = y.toFloat()

    return (0..steps).map {
        val ret = x.roundToInt() coord y.roundToInt()
        x += stepX
        y += stepY
        ret
    }
}

fun MutableList<CompressedPoint>.drawCircle(x: Int, y: Int, p: Int, q: Int) = addAll(
    listOf(
        (x + p) coord (y + q),
        (x - p) coord (y + q),
        (x + p) coord (y - q),
        (x - p) coord (y - q),
        (x + q) coord (y + p),
        (x - q) coord (y + p),
        (x + q) coord (y - p),
        (x - q) coord (y - p)
    )
)

fun circleBres(xc: Int, yc: Int, r: Int): Pair<List<CompressedPoint>, CoordRect> {
    var x = 0
    var y = r
    var d = 3 - 2 * r
    val res = mutableListOf<CompressedPoint>()
    res.drawCircle(xc, yc, x, y)
    while (y >= x) {
        // for each pixel we will
        // draw all eight pixels
        x++

        // check for decision parameter
        // and correspondingly
        // update d, x, y
        d = if (d > 0) {
            y--
            d + 4 * (x - y) + 10
        } else d + 4 * x + 6
        res.drawCircle(xc, yc, x, y)
    }
    val topLeft = (xc - r) coord (yc - r)
    val botRight = (xc + r) coord (yc + r)
    return res.distinct() to (topLeft coordRect botRight)
}

fun List2D<Boolean>.fillPolygon(
    rect: CoordRect,
    fill: Boolean = true,
): List2D<Boolean> {
    var last = false
    var isInShape = 0
    var entering = true

    for (y in (rect.lesser.y + 1)..rect.greater.y) {
        for (x in rect.lesser.x..rect.greater.x) {
            if (y !in 1 until height) continue
            val b = this.at(x, y)
            when {
                b != last -> {
                    if (isInShape == 2) entering = false
                    if (isInShape == 0) entering = true
                    if (entering) isInShape++ else isInShape--
                    if (isInShape > 0) assign(x, y, fill)
                }

                isInShape > 0 -> assign(x, y, fill)
            }
            last = b
        }

        isInShape = 0
        entering = true
        last = false
    }
    return this
}

fun getCircleByBresenham(center: CompressedPoint, radius: Int) {
//    var p = 0
//    var q = radius
//    var r = radius
//    var decision = 3 - 2 * r
//    val results = mutableListOf<Coord>()
//    while (p < q) {
//        results.drawCircle(center.x, center.y, p, q)
//        p++
//        if (decision < 0) {
//            decision += 4 * p + 6
//        } else {
//            r -= 1
//            decision += 4 * (p - q) + 10
//        }
//        results.drawCircle(center.x, center.y, p, q)
//    }
//    val sorted = results.sorted()
//    (0..results.maxBy { it.y }!!.y).forEach { y ->
//        (0..results.maxBy { it.x }!!.x).forEach { x ->
//            if (x coord y in sorted) print("x") else print(".")
//        }
//        println()
//    }
//    println(sorted)
//    println()

    previewCircle()

    val screen = (Array(40) {
        Array(80) { 0 }
    }).toList2D()
    var tick = 0
    forever(1000 / 20) {
        println(HIDE_CURSOR + RIS)
        (0..5).forEach {
            screen.addLayer((it * 192 % 256) shl 16 or 0x00AA88) {
                drawCircle(getRandomCircleCoordinate(it), 5)
            }
        }
        screen.addLayer(0xff0000) {
            drawCircle(((tick++) % 80) coord 20, 10)
        }
        screen.also { it.traverseMap { ColorInt(it) }.printScreenColor() }
        screen.traverseMutate { x, y, i -> 0 }
    }
}

fun forever(limiter: Int? = 100, action: () -> Unit) {
    while (true) {
        action()
        limiter?.let { Thread.sleep(it.toLong()) }
    }
}


fun List2D<Boolean>.drawCircle(
    center: CompressedPoint,
    radius: Int
) {
    circleBres(center.x, center.y, radius).also {
        val (list, _) = it
        traverseAssign(list, true)
    }
}

fun List2D<Boolean>.drawLine(
    start: CompressedPoint,
    end: CompressedPoint
) {
    start.lineByDDATo(end).also { traverseAssign(it, true) }
}

fun List2D<Boolean>.drawCircleFilled(
    center: CompressedPoint,
    radius: Int
) {
    circleBres(center.x, center.y, radius).also {
        val (list, rect) = it
        traverseAssign(list, true)
        this.fillPolygon(rect.modifyBy(gy = -1), true)
    }
}

private fun previewCircle() {
    val screen = (Array(43) {
        Array(80) { false }
    }).toList2D()
//    circleBres(20, 20, 10).also { pair ->
//        val (list, rect) = pair
//        screen.traverseAssign(list, true)
//
//        printScreen(screen)
//
//        println()
//
//        screen.fillPolygon(rect.modifyBy(gy = -1), true)
//        printScreen(screen)
//    }

    screen.drawCircleFilled(30 coord 30, 10)
    screen.drawCircle(20 coord 20, 10)
    screen.printScreen()

    val screen2 = screen.traverseMap { false }
    screen2.drawCircleFilled(35 coord 35, 10)

    val merge =
        screen.mergeWith(screen2, false) { first: Boolean, second: Boolean -> first xor second }
    merge.printScreen()

    val screenColor = screen
        .traverseMapIndexed { x, y, t -> x shl 16 or y shl 8 }
        .also {
            it.traverseMap { ColorInt(it) }.printScreenColor()
        }

    val screenColor2 = screen
        .traverseMap { t -> if (t) 0xFF0000 else 0 }

    val also = screenColor.mergeWith(screenColor2, 0) { first: Int, second: Int ->
        if (second != 0) second else first
    }.also {
        it.traverseMap { ColorInt(it) }.printScreenColor()
    }

    val traverseMap = screen.traverseMap { false }
    traverseMap.drawLine(23 coord 20, 40 coord 75)
    (also to traverseMap).merge(0) { first, second ->
        if (second) 0xffffff else first
    }.also {
        it.traverseMap { ColorInt(it) }.printScreenColor()
    }

    (0..5).forEach {
        also.addLayer((it * 192 % 256) shl 16 or 0x00AA88) {
            drawCircle(
                getRandomCircleCoordinate(it),
                5
            )
        }
    }
    also.also {
        it.traverseMap { ColorInt(it) }.printScreenColor()
    }

//    circleBres(40, 30, 20).also {
//        val (list, rect) = it
//        screen.traverseAssign(list, true)
//        screen.fillPolygon(rect.modifyBy(gy = -1), true)
//    }
//
//    printScreen(screen)
}

private fun getRandomCircleCoordinate(it: Int) =
    it * 7 + (Random.nextInt() % 6) coord 32 + (Random.nextInt() % 20) * if (Random.nextBoolean()) 1 else -1

private fun List2D<Boolean>.printScreen() {
    traverseMap { t -> if (t) "XX" else ".." }.printAll("")
}

private fun List2D<ColorInt>.printScreenColor() {
    traverseMap { t ->
        "  ".style(
            colorBackground = AnsiColor.Custom(t)
        )
    }.printAll("")
}

inline fun <reified T> Array<Array<T>>.toList2D() = List2D.from(map { rows -> rows.toList() })

inline fun <reified T, reified S, reified R> Pair<List2D<T>, List2D<S>>.merge(
    default: R,
    crossinline onElement: (first: T, second: S) -> R,
): List2D<R> = first.mergeWith(second, default, onElement)

fun List2D<Int>.addLayer(
    color: Int,
    config: List2D<Boolean>.() -> Unit,
) {
    val other = traverseMap { false }.also(config)
    this.traverseMutate { x, y, each ->
        if (other.isValidCoordinate(x coord y)) {
            if (other.at(x, y)) return@traverseMutate color
        }
        return@traverseMutate each
    }
}
package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.types.CompressedPoint
import org.junit.Assert
import org.junit.Test

internal class CompressedPointTest {

    @Test
    fun `generate from 1L`() {
        val subject = CompressedPoint(1L)
        Assert.assertEquals(0, subject.x)
        Assert.assertEquals(1, subject.y)
    }

    @Test
    fun `generate from from 1 to 2`() {
        val subject = CompressedPoint.from(1, 2)
        Assert.assertEquals(1, subject.x)
        Assert.assertEquals(2, subject.y)
    }

    @Test
    fun `generate from from 1728372628 to 373828738`() {
        val subject = CompressedPoint.from(POSITIVE_LARGE_1, POSITIVE_LARGE_2)
        Assert.assertEquals(POSITIVE_LARGE_1, subject.x)
        Assert.assertEquals(POSITIVE_LARGE_2, subject.y)
    }

    @Test
    fun `generate from from MAX_VALUE MAX_VALUE`() {
        val subject = CompressedPoint.from(Int.MAX_VALUE, Int.MAX_VALUE)
        Assert.assertEquals(Int.MAX_VALUE, subject.x)
        Assert.assertEquals(Int.MAX_VALUE, subject.y)
    }

    @Test
    fun `generate from from MIN_VALUE to MIN_VALUE`() {
        val subject = CompressedPoint.from(Int.MIN_VALUE, Int.MIN_VALUE)
        Assert.assertEquals(Int.MIN_VALUE, subject.x)
        Assert.assertEquals(Int.MIN_VALUE, subject.y)
    }

    @Test
    fun `generate from from -100 to MIN_VALUE`() {
        val subject = CompressedPoint.from(NEGATIVE_MEDIUM, Int.MIN_VALUE)
        Assert.assertEquals(NEGATIVE_MEDIUM, subject.x)
        Assert.assertEquals(Int.MIN_VALUE, subject.y)
    }

    @Test
    fun `generate from from -2 to 10`() {
        val subject = CompressedPoint.from(NEGATIVE_TINY, POSITIVE_SMALL)
        Assert.assertEquals(NEGATIVE_TINY, subject.x)
        Assert.assertEquals(POSITIVE_SMALL, subject.y)
    }

    @Test
    fun `generate from from MAX_VALUE to MIN_VALUE`() {
        val subject = CompressedPoint.from(Int.MAX_VALUE, Int.MIN_VALUE)
        Assert.assertEquals(Int.MAX_VALUE, subject.x)
        Assert.assertEquals(Int.MIN_VALUE, subject.y)
    }

    @Test
    fun `generate from from -10, -10`() {
        val subject = CompressedPoint.from(NEGATIVE_SMALL, NEGATIVE_SMALL)
        Assert.assertEquals(NEGATIVE_SMALL, subject.x)
        Assert.assertEquals(NEGATIVE_SMALL, subject.y)
    }

    companion object {
        private const val NEGATIVE_MEDIUM = -100
        private const val NEGATIVE_SMALL = -10
        private const val NEGATIVE_TINY = -2
        private const val POSITIVE_LARGE_1 = 1728372628
        private const val POSITIVE_LARGE_2 = 373828738
        private const val POSITIVE_SMALL = 10
    }
}

package dev.patbeagan.consolevision.types.annotation

/**
 * Multiplatform projects can't have inline value classes,
 * because the JVM does not yet support it without @JvmInline.
 */
@Retention(AnnotationRetention.SOURCE)
annotation class WillInline
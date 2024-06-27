package net.pfiers.shrinkwrap.util

/**
 * Take this sequence if it has at least n elements, else, return null.
 */
fun <T> Sequence<T>.takeOnlyIfAtLeast(count: Int): Sequence<T>? {
    val iterator = iterator()
    val firstCountValues = iterator.asSequence().take(count).toList()
    if (firstCountValues.size < count) return null
    return sequence {
        yieldAll(firstCountValues)
        yieldAll(iterator)
    }
}

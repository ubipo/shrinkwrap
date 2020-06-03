package net.pfiers.shrinkwrap.util

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sign
import kotlin.reflect.KClass

fun toComparableInt(float: Double): Int {
    return (abs(ceil(float)) * sign(float)).toInt()
}

fun <T> getInList(list: List<T>, index: Int): T {
    return when {
        index < 0 -> getInList(list, list.size + index)
        else -> list[index % list.size]
    }
}

inline fun doesntThrow(exceptionType: KClass<out Exception>, throwingBlock: () -> Unit): Boolean {
    return try {
        throwingBlock()
        true
    } catch (ex: Exception) {
        if (exceptionType.isInstance(ex)) {
            return false
        }
        throw ex
    }
}

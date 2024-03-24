package net.pfiers.shrinkwrap.util

import kotlin.reflect.KClass

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

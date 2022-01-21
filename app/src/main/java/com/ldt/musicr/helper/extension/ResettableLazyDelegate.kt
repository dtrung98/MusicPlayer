package com.ldt.musicr.helper.extension

/**
 * Represents a value with lazy initialization like [Lazy]
 * But also support RESET that value to re-initialize it again on NEXT TIME ACCESSED.
 *
 * To create an instance of [ResettableLazy] use the [resettableLazy] function.
 */
interface ResettableLazy<out T> : Lazy<T> {
    fun reset()
}

/**
 * Creates a new instance of the [ResettableLazy] that uses the specified initialization function [initializer]
 * and the default thread-safety mode [LazyThreadSafetyMode.SYNCHRONIZED].
 */
fun <T> resettableLazy(manager: ResettableLazyManager?, initializer: () -> T): ResettableLazy<T> = ResettableSynchronizedLazyImpl(manager, initializer)
fun <T> resettableLazy(initializer: () -> T): ResettableLazy<T> = ResettableSynchronizedLazyImpl(null, initializer)

/**
 * An implementation of [ResettableLazy] that uses the specified initialization function [initializer]
 * and the default thread-safety mode [LazyThreadSafetyMode.SYNCHRONIZED].
 *
 * If the initialization of a value throws an exception, it will attempt to reinitialize the value at next access.
 *
 * Note that the returned instance uses itself to synchronize on. Do not synchronize from external code on
 * the returned instance as it may cause accidental deadlock. Also this behavior can be changed in the future.
 */
private class ResettableSynchronizedLazyImpl<out T>(
        val manager: ResettableLazyManager?,
        private val initializer: () -> T,
        lock: Any? = null
) : ResettableLazy<T> {

    private object UNINITIALIZED_VALUE

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer()
                    _value = typedValue
                    manager?.register(this as ResettableLazy<Any>)
                    typedValue
                }
            }
        }

    override fun reset() {
        synchronized(lock) {
            _value = UNINITIALIZED_VALUE
        }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}
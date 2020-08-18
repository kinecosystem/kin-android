package org.kin.sdk.base.tools


@Suppress("UNCHECKED_CAST")
class Optional<T> private constructor(private val _value: T) {

    fun get(): T? {
        return _value
    }

    fun <S> map(map: (T) -> S): Optional<S> {
        return try {
            if (_value == null) empty() else of(
                map(_value)
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun <S> mapNullable(map: (T) -> S): Optional<S> {
        return try {
            if (_value == null) empty() else ofNullable(
                map(_value)
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val isPresent: Boolean
        get() = _value != null

    fun orElse(other: T): T {
        return _value ?: other
    }

    fun orElse(other: () -> T): T {
        return _value ?: other()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Optional<*>) {
            return false
        }
        if (_value === other._value) {
            return true
        } else if (_value == null || other._value == null) {
            return false
        }
        return _value == other._value
    }

    override fun hashCode(): Int {
        return if (_value == null) {
            0
        } else _value.hashCode() xor OPTIONAL_HASHCODE_MIX
    }

    override fun toString(): String {
        return if (_value == null) {
            "Optional(null)"
        } else "Optional($_value)"
    }

    companion object {
        private const val OPTIONAL_HASHCODE_MIX = -0x40df9289
        private val INVALID: Optional<*> = Optional<Any?>(null)
        fun <T> empty(): Optional<T> {
            return INVALID as Optional<T>
        }

        @JvmStatic
        fun <T> of(value: T): Optional<T> {
            return Optional(value)
        }

        @JvmStatic
        fun <T> ofNullable(value: T?): Optional<T> {
            return value?.let { of(it) } ?: empty<Any>() as Optional<T>
        }
    }
}

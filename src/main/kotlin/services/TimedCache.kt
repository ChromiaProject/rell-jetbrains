package net.postchain.rellide.jetbrains.services

class TimedCache<K, V>(private val durationMs: Long) {
    private data class CacheEntry<T>(val data: T, val timestamp: Long)

    private val cache = mutableMapOf<K, CacheEntry<V>>()

    val size: Int
        get() = cache.size

    private fun isCacheValid(timestamp: Long): Boolean = System.currentTimeMillis() - timestamp < durationMs

    fun get(key: K): V? {
        val entry = cache[key]
        return if (entry != null && isCacheValid(entry.timestamp)) {
            entry.data
        } else {
            null
        }
    }

    fun put(key: K, value: V) {
        cache[key] = CacheEntry(value, System.currentTimeMillis())
    }

    fun putAll(from: Map<out K, V>) {
        cache.putAll(from.mapValues { CacheEntry(it.value, System.currentTimeMillis()) })
    }

    fun remove(key: K): V? = cache.remove(key)?.data
    fun isEmpty(): Boolean = cache.isEmpty()
    fun containsKey(key: K): Boolean = cache.containsKey(key)

    fun clear() {
        cache.clear()
    }
}

inline fun <K, V> TimedCache<K, V>.getOrPut(key: K, supplier: () -> V): V {
    val cached = get(key)
    return if (cached != null) {
        cached
    } else {
        val value = supplier()
        put(key, value)
        value
    }
}

package net.postchain.rellide.jetbrains.services

class TimedCache<K, V>(private val durationMs: Long) {
    private data class CacheEntry<T>(val data: T, val timestamp: Long)
    
    private val cache = mutableMapOf<K, CacheEntry<V>>()
    
    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < durationMs
    }
    
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
    
    fun getOrPut(key: K, supplier: () -> V): V {
        val cached = get(key)
        return if (cached != null) {
            cached
        } else {
            val value = supplier()
            put(key, value)
            value
        }
    }
    
    fun clear() {
        cache.clear()
    }
}

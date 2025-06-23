package net.postchain.rellide.jetbrains.util

class CachedComputation(
        private val compute: (String) -> String,
        private val cacheDurationMs: Long
) {
    private val cache = mutableMapOf<String, CachedResult>()

    fun computeWithCache(input: String): String {
        val now = System.currentTimeMillis()
        val cached = cache[input]

        return if (cached != null && now - cached.timestamp <= cacheDurationMs) {
            cached.result
        } else {
            val result = compute(input)
            cache[input] = CachedResult(result, now)
            result
        }
    }

    private data class CachedResult(val result: String, val timestamp: Long)
}

package com.example.data.ai

import android.util.LruCache

/**
 * High-speed in-memory semantic cache for Mitti.
 * Stores verified craft queries and answers to serve repeated user questions in <50ms.
 */
object MittiCacheManager {
    // LRU Cache storing up to 200 craft query responses
    private val responseCache = LruCache<String, AiResponseResult>(200)

    private fun normalizeKey(query: String): String {
        return query.lowercase()
            .replace(Regex("[^a-z0-9 ]"), "")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    fun get(query: String): AiResponseResult? {
        val key = normalizeKey(query)
        return responseCache.get(key)
    }

    fun put(query: String, result: AiResponseResult) {
        if (query.isBlank() || result.answerText.isBlank()) return
        val key = normalizeKey(query)
        responseCache.put(key, result)
    }

    fun clear() {
        responseCache.evictAll()
    }
}

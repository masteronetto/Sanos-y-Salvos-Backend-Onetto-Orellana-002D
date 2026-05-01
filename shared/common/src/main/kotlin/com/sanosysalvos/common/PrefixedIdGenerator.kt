package com.sanosysalvos.common

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object PrefixedIdGenerator {
    private val counters = ConcurrentHashMap<String, AtomicInteger>()

    fun next(prefix: String, existingIds: Collection<String?> = emptyList()): String {
        val maxExisting = existingIds
            .asSequence()
            .mapNotNull { extractNumericSuffix(prefix, it) }
            .maxOrNull() ?: 0

        val counter = counters.computeIfAbsent(prefix) { AtomicInteger(maxExisting) }

        while (true) {
            val current = counter.get()
            if (current < maxExisting) {
                if (counter.compareAndSet(current, maxExisting)) {
                    continue
                }
            }
            val nextNumber = counter.incrementAndGet()
            return format(prefix, nextNumber)
        }
    }

    private fun extractNumericSuffix(prefix: String, value: String?): Int? {
        if (value.isNullOrBlank() || !value.startsWith(prefix)) return null
        val suffix = value.removePrefix(prefix)
        return suffix.toIntOrNull()
    }

    private fun format(prefix: String, number: Int): String =
        prefix + number.toString().padStart(3, '0')
}

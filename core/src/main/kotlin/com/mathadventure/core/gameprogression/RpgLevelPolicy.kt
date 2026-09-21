package com.mathadventure.core.gameprogression

class DefaultRpgLevelPolicy : RpgLevelPolicy {
    override fun levelFor(totalXp: Long): Int {
        require(totalXp >= 0) { "totalXp must be non-negative" }
        var level = 1
        while (level < 30 && totalXp >= requiredXp(level + 1)) level++
        return level
    }

    private fun requiredXp(level: Int): Long =
        50L * (level - 1L) * (level - 1L)
}

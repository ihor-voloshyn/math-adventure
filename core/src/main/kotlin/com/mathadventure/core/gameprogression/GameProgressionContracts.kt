package com.mathadventure.core.gameprogression

interface GameRewardPolicy {
    fun evaluate(event: GameProgressionEvent, state: GameProgressionState): ProgressionEvaluation
}

interface RpgLevelPolicy {
    fun levelFor(totalXp: Long): Int
}

interface GameUnlockPolicy {
    fun unlocksFor(levelBefore: Int, levelAfter: Int): Set<String>
}

interface GameProgressionStore {
    fun get(playerId: String): GameProgressionState

    /** Atomically persists event effects and must be idempotent by event.eventId. */
    fun commit(commit: ProgressionCommit)
}

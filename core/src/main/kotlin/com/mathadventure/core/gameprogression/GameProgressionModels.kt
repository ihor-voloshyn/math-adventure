package com.mathadventure.core.gameprogression

enum class GameEventType {
    QUEST_COMPLETED,
    COMBAT_VICTORY,
    DISCOVERY,
    PUZZLE_COMPLETED,
    STORY_MILESTONE,
    HOME_MILESTONE,
    ACHIEVEMENT,
    LEVEL_COMPLETED
}

data class GameProgressionEvent(
    val eventId: String,
    val playerId: String,
    val eventType: GameEventType,
    val sourceId: String,
    val sessionId: String,
    val outcome: String,
    val timestampEpochMillis: Long,
    val metadata: Map<String, String> = emptyMap()
)

data class BestResult(
    val sourceId: String,
    val outcome: String,
    val score: Int? = null,
    val completionState: String = "COMPLETED"
)

data class GameProgressionState(
    val totalXp: Long = 0L,
    val coins: Long = 0L,
    val rpgLevel: Int = 1,
    val bestResults: Map<String, BestResult> = emptyMap(),
    val grantedEventIds: Set<String> = emptySet(),
    val unlockedIds: Set<String> = emptySet()
) {
    init {
        require(totalXp >= 0) { "totalXp must be non-negative" }
        require(coins >= 0) { "coins must be non-negative" }
        require(rpgLevel in 1..30) { "rpgLevel must be between 1 and 30" }
    }
}

data class RewardBundle(
    val xpDelta: Long = 0L,
    val coinsDelta: Long = 0L,
    val lootIds: List<String> = emptyList(),
    val reason: String
) {
    init {
        require(xpDelta >= 0) { "xpDelta must be non-negative" }
        require(coinsDelta >= 0) { "coinsDelta must be non-negative" }
    }
}

data class ProgressionCommit(
    val event: GameProgressionEvent,
    val reward: RewardBundle,
    val levelBefore: Int,
    val levelAfter: Int,
    val unlocks: Set<String>,
    val bestResultAfter: BestResult?
)

data class ProgressionEvaluation(
    val eligible: Boolean,
    val reward: RewardBundle,
    val bestResultAfter: BestResult?,
    val reason: String
)
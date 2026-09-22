package com.mathadventure.core.quest

enum class QuestType { STORY, ADVENTURE, EXPLORATION, COLLECTION, COMBAT, DISCOVERY, HOME_TERRITORY }
enum class QuestState { LOCKED, AVAILABLE, ACTIVE, COMPLETED, FAILED, ABANDONED }
enum class QuestRepeatability { ONE_TIME, REPEATABLE, REPEATABLE_LIMITED_REWARD }

data class QuestObjectiveDefinition(
    val id: String,
    val descriptionKey: String,
    val requiredCount: Int = 1
) {
    init {
        require(id.isNotBlank()) { "objective id must not be blank" }
        require(requiredCount > 0) { "requiredCount must be positive" }
    }
}

data class QuestDefinition(
    val id: String,
    val titleKey: String,
    val descriptionKey: String,
    val type: QuestType,
    val objectives: List<QuestObjectiveDefinition>,
    val recommendedRpgLevel: Int = 1,
    val repeatability: QuestRepeatability = QuestRepeatability.ONE_TIME,
    val prerequisites: Set<String> = emptySet()
) {
    init {
        require(id.isNotBlank()) { "quest id must not be blank" }
        require(objectives.isNotEmpty()) { "quest must have at least one objective" }
        require(recommendedRpgLevel in 1..30) { "recommendedRpgLevel must be 1..30" }
        require(objectives.map { it.id }.distinct().size == objectives.size) { "objective ids must be unique" }
    }
}

data class QuestInstance(
    val questId: String,
    val playerId: String,
    val state: QuestState,
    val objectivesProgress: Map<String, Int>,
    val sessionId: String? = null,
    val startedAtEpochMillis: Long? = null,
    val completedAtEpochMillis: Long? = null
)

data class QuestCompletion(
    val questId: String,
    val playerId: String,
    val instanceId: String,
    val eventId: String,
    val completedAtEpochMillis: Long
)

interface QuestStore {
    fun get(playerId: String, questId: String): QuestInstance?
    fun save(instance: QuestInstance)
}

interface QuestPrerequisiteChecker {
    fun isSatisfied(playerId: String, prerequisites: Set<String>): Boolean
}

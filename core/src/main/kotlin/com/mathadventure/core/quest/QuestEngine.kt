package com.mathadventure.core.quest

class QuestEngine(
    private val definitions: Map<String, QuestDefinition>,
    private val store: QuestStore,
    private val prerequisiteChecker: QuestPrerequisiteChecker
) {
    fun availability(playerId: String, questId: String): QuestState {
        val definition = definition(questId)
        val current = store.get(playerId, questId)
        return when {
            current?.state == QuestState.COMPLETED && definition.repeatability == QuestRepeatability.ONE_TIME -> QuestState.COMPLETED
            current?.state == QuestState.ACTIVE -> QuestState.ACTIVE
            prerequisiteChecker.isSatisfied(playerId, definition.prerequisites) -> QuestState.AVAILABLE
            else -> QuestState.LOCKED
        }
    }

    fun start(playerId: String, questId: String, sessionId: String, nowEpochMillis: Long): QuestInstance {
        val definition = definition(questId)
        require(availability(playerId, questId) == QuestState.AVAILABLE) { "quest is not available: $questId" }
        val instance = QuestInstance(
            questId = questId,
            playerId = playerId,
            state = QuestState.ACTIVE,
            objectivesProgress = definition.objectives.associate { it.id to 0 },
            sessionId = sessionId,
            startedAtEpochMillis = nowEpochMillis
        )
        store.save(instance)
        return instance
    }

    fun recordObjectiveProgress(
        playerId: String,
        questId: String,
        objectiveId: String,
        delta: Int = 1,
        nowEpochMillis: Long
    ): QuestInstance {
        require(delta > 0) { "delta must be positive" }
        val definition = definition(questId)
        val objective = definition.objectives.firstOrNull { it.id == objectiveId }
            ?: error("unknown objective: $objectiveId")
        val current = store.get(playerId, questId) ?: error("quest instance does not exist: $questId")
        require(current.state == QuestState.ACTIVE) { "quest is not active: $questId" }

        val progress = current.objectivesProgress.toMutableMap()
        val next = ((progress[objectiveId] ?: 0) + delta).coerceAtMost(objective.requiredCount)
        progress[objectiveId] = next

        val completed = definition.objectives.all { (progress[it.id] ?: 0) >= it.requiredCount }
        val updated = current.copy(
            state = if (completed) QuestState.COMPLETED else QuestState.ACTIVE,
            objectivesProgress = progress.toMap(),
            completedAtEpochMillis = if (completed) nowEpochMillis else null
        )
        store.save(updated)
        return updated
    }

    fun completion(playerId: String, questId: String, instanceId: String): QuestCompletion? {
        val instance = store.get(playerId, questId) ?: return null
        if (instance.state != QuestState.COMPLETED) return null
        require(instance.sessionId == instanceId) { "completion instance does not match stored quest session" }
        return QuestCompletion(
            questId = questId,
            playerId = playerId,
            instanceId = instanceId,
            eventId = "quest-completed-$instanceId",
            completedAtEpochMillis = instance.completedAtEpochMillis ?: error("completed quest has no completion time")
        )
    }

    private fun definition(questId: String): QuestDefinition =
        definitions[questId] ?: error("unknown quest: $questId")
}

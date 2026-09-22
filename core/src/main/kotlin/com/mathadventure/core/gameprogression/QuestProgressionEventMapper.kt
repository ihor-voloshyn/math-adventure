package com.mathadventure.core.gameprogression

import com.mathadventure.core.quest.QuestCompletion
import com.mathadventure.core.quest.QuestRepeatability

/**
 * Boundary adapter: Quest owns completion semantics; Game Progression owns reward semantics.
 */
object QuestProgressionEventMapper {
    fun map(
        completion: QuestCompletion,
        repeatability: QuestRepeatability
    ): GameProgressionEvent =
        GameProgressionEvent(
            eventId = completion.eventId,
            playerId = completion.playerId,
            eventType = GameEventType.QUEST_COMPLETED,
            sourceId = completion.questId,
            sessionId = completion.instanceId,
            outcome = "COMPLETED",
            timestampEpochMillis = completion.completedAtEpochMillis,
            metadata = mapOf("repeatability" to repeatability.name)
        )
}

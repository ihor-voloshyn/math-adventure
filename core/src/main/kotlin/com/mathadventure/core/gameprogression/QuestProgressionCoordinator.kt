package com.mathadventure.core.gameprogression

interface QuestCompletionOutbox {
    fun save(event: GameProgressionEvent)
    fun pending(playerId: String): List<GameProgressionEvent>
    fun remove(eventId: String)
}

class QuestProgressionCoordinator(
    private val outbox: QuestCompletionOutbox,
    private val progression: CoreGameProgressionFlow
) {
    fun record(event: GameProgressionEvent): ProgressionCommit? {
        outbox.save(event)
        return try {
            val commit = progression.record(event)
            outbox.remove(event.eventId)
            commit
        } catch (error: Throwable) {
            throw error
        }
    }

    fun recover(playerId: String): List<ProgressionCommit?> =
        outbox.pending(playerId).map { event ->
            try {
                val commit = progression.record(event)
                outbox.remove(event.eventId)
                commit
            } catch (error: Throwable) {
                throw error
            }
        }
}

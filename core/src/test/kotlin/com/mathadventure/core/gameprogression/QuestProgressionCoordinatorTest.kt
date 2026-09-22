package com.mathadventure.core.gameprogression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QuestProgressionCoordinatorTest {
    @Test
    fun failedProgressionLeavesEventForRecovery() {
        val outbox = MemoryOutbox()
        val progression = FailingOnceProgression()
        val coordinator = QuestProgressionCoordinator(outbox, progression)
        val event = event()

        kotlin.test.assertFailsWith<IllegalStateException> {
            coordinator.record(event)
        }
        assertEquals(listOf(event), outbox.pending("player-1"))

        val recovered = coordinator.recover("player-1")
        assertEquals(1, recovered.size)
        assertEquals(emptyList(), outbox.pending("player-1"))
        assertEquals(1, progression.recordedEventIds.size)
    }

    @Test
    fun recoveryIsSafeWhenProgressionAlreadyCommittedBeforeCleanup() {
        val outbox = MemoryOutbox()
        val progression = RecordingProgression()
        val event = event()
        outbox.save(event)
        progression.record(event)

        val recovered = QuestProgressionCoordinator(outbox, progression).recover("player-1")

        assertEquals(1, recovered.size)
        assertNull(recovered.single())
        assertEquals(emptyList(), outbox.pending("player-1"))
        assertEquals(listOf(event.eventId), progression.recordedEventIds)
    }

    private fun event() = GameProgressionEvent(
        eventId = "quest-completed-session-1",
        playerId = "player-1",
        eventType = GameEventType.QUEST_COMPLETED,
        sourceId = "story_home_to_village",
        sessionId = "session-1",
        outcome = "COMPLETED",
        timestampEpochMillis = 100L,
        metadata = mapOf("repeatability" to "ONE_TIME")
    )

    private class MemoryOutbox : QuestCompletionOutbox {
        private val events = linkedMapOf<String, GameProgressionEvent>()
        override fun save(event: GameProgressionEvent) { events[event.eventId] = event }
        override fun pending(playerId: String) = events.values.filter { it.playerId == playerId }
        override fun remove(eventId: String) { events.remove(eventId) }
    }

    private class FailingOnceProgression : CoreGameProgressionFlow(
        object : GameRewardPolicy {
            override fun evaluate(event: GameProgressionEvent, state: GameProgressionState) =
                ProgressionEvaluation(true, RewardBundle(xpDelta = 1L, reason = "test"), null, "test")
        },
        object : RpgLevelPolicy {
            override fun levelFor(totalXp: Long) = 1
        },
        object : GameUnlockPolicy {
            override fun unlocksFor(levelBefore: Int, levelAfter: Int) = emptySet<String>()
        },
        RecordingStore()
    ) {
        private var fail = true
        val recordedEventIds = mutableListOf<String>()

        override fun record(event: GameProgressionEvent): ProgressionCommit? {
            if (fail) {
                fail = false
                throw IllegalStateException("simulated crash")
            }
            recordedEventIds += event.eventId
            return super.record(event)
        }
    }

    private class RecordingProgression : CoreGameProgressionFlow(
        object : GameRewardPolicy {
            override fun evaluate(event: GameProgressionEvent, state: GameProgressionState) =
                ProgressionEvaluation(
                    eligible = false,
                    reward = RewardBundle(reason = "already-granted"),
                    bestResultAfter = null,
                    reason = "already-granted"
                )
        },
        object : RpgLevelPolicy {
            override fun levelFor(totalXp: Long) = 1
        },
        object : GameUnlockPolicy {
            override fun unlocksFor(levelBefore: Int, levelAfter: Int) = emptySet<String>()
        },
        RecordingStore()
    ) {
        val recordedEventIds = mutableListOf<String>()
        override fun record(event: GameProgressionEvent): ProgressionCommit? {
            recordedEventIds += event.eventId
            return null
        }
    }

    private class RecordingStore : GameProgressionStore {
        override fun get(playerId: String) = GameProgressionState()
        override fun commit(commit: ProgressionCommit) = Unit
    }
}

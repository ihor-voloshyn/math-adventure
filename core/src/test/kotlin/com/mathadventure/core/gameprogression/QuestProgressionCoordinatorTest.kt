package com.mathadventure.core.gameprogression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class QuestProgressionCoordinatorTest {
    @Test
    fun failedProgressionLeavesEventForRecovery() {
        val outbox = MemoryOutbox()
        val store = FailingOnceStore()
        val coordinator = QuestProgressionCoordinator(outbox, flow(store))
        val event = event()

        assertFailsWith<IllegalStateException> { coordinator.record(event) }
        assertEquals(listOf(event), outbox.pending("player-1"))

        val recovered = coordinator.recover("player-1")
        assertEquals(1, recovered.size)
        assertEquals(1, recovered[0]!!.reward.xpDelta)
        assertEquals(emptyList(), outbox.pending("player-1"))
        assertEquals(1L, store.state.totalXp)
    }

    @Test
    fun recoveryIsSafeWhenProgressionAlreadyCommittedBeforeCleanup() {
        val outbox = MemoryOutbox()
        val store = RecordingStore()
        val event = event()
        store.state = store.state.copy(grantedEventIds = setOf(event.eventId))
        outbox.save(event)

        val recovered = QuestProgressionCoordinator(outbox, flow(store)).recover("player-1")

        assertEquals(1, recovered.size)
        assertNull(recovered.single())
        assertEquals(emptyList(), outbox.pending("player-1"))
        assertEquals(setOf(event.eventId), store.state.grantedEventIds)
    }

    private fun flow(store: GameProgressionStore) = CoreGameProgressionFlow(
        rewardPolicy = object : GameRewardPolicy {
            override fun evaluate(
                event: GameProgressionEvent,
                state: GameProgressionState
            ) = ProgressionEvaluation(
                eligible = true,
                reward = RewardBundle(xpDelta = 1L, reason = "test"),
                bestResultAfter = null,
                reason = "test"
            )
        },
        levelPolicy = DefaultRpgLevelPolicy(),
        unlockPolicy = PrototypeGameUnlockPolicy(),
        store = store
    )

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

    private open class RecordingStore : GameProgressionStore {
        var state = GameProgressionState()
        override fun get(playerId: String) = state
        override fun commit(commit: ProgressionCommit) {
            state = state.copy(
                totalXp = state.totalXp + commit.reward.xpDelta,
                coins = state.coins + commit.reward.coinsDelta,
                rpgLevel = commit.levelAfter,
                grantedEventIds = state.grantedEventIds + commit.event.eventId,
                unlockedIds = state.unlockedIds + commit.unlocks
            )
        }
    }

    private class FailingOnceStore : RecordingStore() {
        private var fail = true
        override fun commit(commit: ProgressionCommit) {
            if (fail) {
                fail = false
                throw IllegalStateException("simulated crash")
            }
            super.commit(commit)
        }
    }
}

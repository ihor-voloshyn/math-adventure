package com.mathadventure.core.gameprogression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameProgressionTest {
    private val event = GameProgressionEvent(
        eventId = "event-1",
        playerId = "player-1",
        eventType = GameEventType.COMBAT_VICTORY,
        sourceId = "goblin-encounter-1",
        sessionId = "session-1",
        outcome = "VICTORY",
        timestampEpochMillis = 1_000L
    )

    @Test
    fun rpgLevelUsesApprovedCumulativeXpThresholds() {
        val policy = DefaultRpgLevelPolicy()
        assertEquals(1, policy.levelFor(0))
        assertEquals(2, policy.levelFor(50))
        assertEquals(5, policy.levelFor(800))
        assertEquals(10, policy.levelFor(4_050))
        assertEquals(30, policy.levelFor(42_050))
        assertEquals(30, policy.levelFor(Long.MAX_VALUE))
    }

    @Test
    fun duplicateEventIsNotRewardedTwice() {
        val store = RecordingStore()
        val flow = CoreGameProgressionFlow(FixedRewardPolicy(), DefaultRpgLevelPolicy(), EmptyUnlockPolicy(), store)
        assertEquals(true, flow.record(event) != null)
        assertNull(flow.record(event))
        assertEquals(1, store.commits.size)
    }

    @Test
    fun oneTimeQuestCompletionIsRewardedAndMappedByQuestId() {
        val store = RecordingStore()
        val flow = CoreGameProgressionFlow(
            PrototypeGameRewardPolicy(),
            DefaultRpgLevelPolicy(),
            EmptyUnlockPolicy(),
            store
        )
        val questEvent = QuestProgressionEventMapper.map(
            com.mathadventure.core.quest.QuestCompletion(
                questId = "story_home_to_village",
                playerId = "player-1",
                instanceId = "session-1",
                eventId = "quest-completed-session-1",
                completedAtEpochMillis = 2_000L
            ),
            com.mathadventure.core.quest.QuestRepeatability.ONE_TIME
        )

        val first = flow.record(questEvent)
        val duplicate = flow.record(questEvent)

        assertEquals(GameEventType.QUEST_COMPLETED, questEvent.eventType)
        assertEquals("story_home_to_village", questEvent.sourceId)
        assertEquals(75L, first!!.reward.xpDelta)
        assertEquals(15L, first.reward.coinsDelta)
        assertNull(duplicate)
    }

    @Test
    fun repeatableQuestRewardIsNotGrantedByPrototypePolicy() {
        val store = RecordingStore()
        val flow = CoreGameProgressionFlow(
            PrototypeGameRewardPolicy(),
            DefaultRpgLevelPolicy(),
            EmptyUnlockPolicy(),
            store
        )
        val questEvent = QuestProgressionEventMapper.map(
            com.mathadventure.core.quest.QuestCompletion(
                questId = "repeatable-quest",
                playerId = "player-1",
                instanceId = "session-2",
                eventId = "quest-completed-session-2",
                completedAtEpochMillis = 3_000L
            ),
            com.mathadventure.core.quest.QuestRepeatability.REPEATABLE
        )

        assertNull(flow.record(questEvent))
        assertEquals(0, store.commits.size)
    }

    @Test
    fun rewardFlowIsIndependentFromMathMastery() {
        val store = RecordingStore()
        val flow = CoreGameProgressionFlow(FixedRewardPolicy(), DefaultRpgLevelPolicy(), EmptyUnlockPolicy(), store)
        val commit = flow.record(event)!!
        assertEquals(100L, commit.reward.xpDelta)
        assertEquals(25L, commit.reward.coinsDelta)
    }

    private class FixedRewardPolicy : GameRewardPolicy {
        override fun evaluate(event: GameProgressionEvent, state: GameProgressionState) =
            ProgressionEvaluation(
                eligible = true,
                reward = RewardBundle(100L, 25L, reason = "test"),
                bestResultAfter = BestResult(event.sourceId, event.outcome),
                reason = "test"
            )
    }

    private class EmptyUnlockPolicy : GameUnlockPolicy {
        override fun unlocksFor(levelBefore: Int, levelAfter: Int) = emptySet<String>()
    }

    private class RecordingStore : GameProgressionStore {
        var state = GameProgressionState()
        val commits = mutableListOf<ProgressionCommit>()

        override fun get(playerId: String) = state

        override fun commit(commit: ProgressionCommit) {
            commits += commit
            state = state.copy(
                totalXp = state.totalXp + commit.reward.xpDelta,
                coins = state.coins + commit.reward.coinsDelta,
                rpgLevel = commit.levelAfter,
                grantedEventIds = state.grantedEventIds + commit.event.eventId
            )
        }
    }
}

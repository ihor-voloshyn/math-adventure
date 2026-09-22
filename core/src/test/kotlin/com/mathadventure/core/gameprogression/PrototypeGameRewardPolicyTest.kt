package com.mathadventure.core.gameprogression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrototypeGameRewardPolicyTest {
    private val policy = PrototypeGameRewardPolicy()

    private val event = GameProgressionEvent(
        eventId = "victory-1",
        playerId = "player-1",
        eventType = GameEventType.COMBAT_VICTORY,
        sourceId = "forest-encounter-01",
        sessionId = "combat-1",
        outcome = "VICTORY",
        timestampEpochMillis = 1L
    )

    @Test
    fun firstCombatVictoryGrantsVerticalSliceReward() {
        val result = policy.evaluate(event, GameProgressionState())

        assertTrue(result.eligible)
        assertEquals(100L, result.reward.xpDelta)
        assertEquals(25L, result.reward.coinsDelta)
        assertEquals("forest-encounter-01", result.bestResultAfter?.sourceId)
    }

    @Test
    fun repeatedCombatSourceGetsNoSecondReward() {
        val state = GameProgressionState(
            bestResults = mapOf(
                event.sourceId to BestResult(event.sourceId, "VICTORY")
            )
        )

        val result = policy.evaluate(event, state)

        assertFalse(result.eligible)
        assertEquals(0L, result.reward.xpDelta)
        assertEquals(0L, result.reward.coinsDelta)
        assertEquals("repeat_no_reward", result.reason)
    }

    @Test
    fun unsupportedEventGetsNoReward() {
        val unsupported = event.copy(
            eventType = GameEventType.DISCOVERY,
            sourceId = "forest-discovery-01"
        )

        val result = policy.evaluate(unsupported, GameProgressionState())

        assertFalse(result.eligible)
        assertEquals("unsupported_event", result.reason)
    }
}

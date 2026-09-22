package com.mathadventure.core.gameprogression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VerticalSliceRewardPolicyTest {
    private val policy = VerticalSliceRewardPolicy()

    @Test fun firstForestVictoryIsRewarded() {
        val state = GameProgressionState()
        val event = event()
        val result = policy.evaluate(event, state)
        assertTrue(result.eligible)
        assertEquals(100L, result.reward.xpDelta)
        assertEquals(25L, result.reward.coinsDelta)
    }

    @Test fun repeatedForestVictoryIsNotRewarded() {
        val event = event()
        val state = GameProgressionState(
            bestResults = mapOf("forest-encounter-01" to BestResult("forest-encounter-01", "VICTORY"))
        )
        val result = policy.evaluate(event, state)
        assertFalse(result.eligible)
    }

    private fun event() = GameProgressionEvent(
        eventId = "victory-1",
        playerId = "prototype-player",
        eventType = GameEventType.COMBAT_VICTORY,
        sourceId = "forest-encounter-01",
        sessionId = "session-1",
        outcome = "VICTORY",
        timestampEpochMillis = 1L
    )
}

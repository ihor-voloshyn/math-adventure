package com.mathadventure.core.combat

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CombatEngineTest {
    private val engine = CombatEngine()

    @Test fun correctAnswerCompletesEncounterImmediately() {
        val result = engine.resolveMathAnswer(engine.start("c1"), true)
        assertEquals(CombatResolution.VICTORY, result.resolution)
        assertEquals(3, result.state.heroHearts)
        assertEquals(0, result.state.attemptsUsed)
        assertFalse(result.state.active)
        assertEquals("COMBAT_VICTORY", result.gameEventType)
    }

    @Test fun incorrectAnswerConsumesOneHeartAndKeepsSameEncounter() {
        val result = engine.resolveMathAnswer(engine.start("c1"), false)
        assertEquals(CombatResolution.INCORRECT, result.resolution)
        assertEquals(2, result.state.heroHearts)
        assertEquals(1, result.state.attemptsUsed)
        assertTrue(result.state.active)
    }

    @Test fun threeIncorrectAnswersDefeatEncounter() {
        var state = engine.start("c1")
        repeat(2) {
            val result = engine.resolveMathAnswer(state, false)
            assertEquals(CombatResolution.INCORRECT, result.resolution)
            state = result.state
        }
        val defeat = engine.resolveMathAnswer(state, false)
        assertEquals(CombatResolution.DEFEAT, defeat.resolution)
        assertEquals(0, defeat.state.heroHearts)
        assertEquals(3, defeat.state.attemptsUsed)
        assertFalse(defeat.state.active)
    }

    @Test fun customHeartLimitIsPreserved() {
        val state = engine.start("c1", heroHearts = 5)
        assertEquals(5, state.heroHearts)
        assertEquals(5, state.maxHeroHearts)
        assertEquals(CombatResolution.INCORRECT, engine.resolveMathAnswer(state, false).resolution)
    }
}

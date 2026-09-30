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

    @Test fun everyEncounterStartsWithExactlyThreeHearts() {
        val first = engine.start("c1")
        val second = engine.start("c2")
        assertEquals(3, first.heroHearts)
        assertEquals(3, first.maxHeroHearts)
        assertEquals(3, second.heroHearts)
        assertEquals(3, second.maxHeroHearts)
    }

    @Test fun correctAnswerPreservesAllRemainingHearts() {
        var state = engine.start("c1")
        state = engine.resolveMathAnswer(state, false).state
        state = engine.resolveMathAnswer(state, true).state
        assertEquals(2, state.heroHearts)
        assertEquals(3, state.maxHeroHearts)
        assertEquals(1, state.attemptsUsed)
        assertFalse(state.active)
    }
}

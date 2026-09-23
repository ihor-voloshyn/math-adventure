package com.mathadventure.core.combat

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CombatEngineTest {
    private val engine = CombatEngine()

    @Test fun correctAttackDamagesEnemy() {
        val result = engine.resolveMathAction(engine.start("c1"), CombatAction.ATTACK, true)
        assertEquals(2, result.state.enemyHp)
        assertEquals(3, result.state.heroHearts)
        assertTrue(result.state.playerTurn)
    }

    @Test fun combatTracksMaximumHeartsForHud() {
        val state = engine.start("c1", heroHearts = 5, enemyHp = 3)
        assertEquals(5, state.heroHearts)
        assertEquals(5, state.maxHeroHearts)

        val result = engine.resolveMathAction(state, CombatAction.ATTACK, true)
        assertEquals(5, result.state.heroHearts)
        assertEquals(5, result.state.maxHeroHearts)
    }

    @Test fun attackUsesResolvedEquipmentDamage() {
        val state = engine.start("c1", heroHearts = 3, enemyHp = 3)
        val result = engine.resolveMathAction(state, CombatAction.ATTACK, true, attackDamage = 2)

        assertEquals(1, result.state.enemyHp)
        assertEquals(3, result.state.heroHearts)
        assertEquals(CombatResolution.HIT, result.resolution)
    }


    @Test fun defendBlocksEnemyDamage() {
        val result = engine.resolveMathAction(engine.start("c1"), CombatAction.DEFEND, true)
        assertEquals(3, result.state.heroHearts)
        assertEquals(3, result.state.enemyHp)
        assertEquals(CombatResolution.BLOCKED, result.resolution)
        assertTrue(result.state.playerTurn)
        assertTrue(result.state.active)
    }



    @Test fun defendBlocksDamageRegardlessOfAnswerFlag() {
        val result = engine.resolveMathAction(engine.start("c1"), CombatAction.DEFEND, false)
        assertEquals(3, result.state.heroHearts)
        assertEquals(3, result.state.enemyHp)
        assertEquals(CombatResolution.BLOCKED, result.resolution)
        assertTrue(result.state.active)
    }

    @Test fun incorrectAnswerMissesButDoesNotInstantlyKill() {
        val result = engine.resolveMathAction(engine.start("c1"), CombatAction.ATTACK, false)
        assertEquals(2, result.state.heroHearts)
        assertEquals(3, result.state.enemyHp)
        assertEquals(CombatResolution.MISS, result.resolution)
        assertTrue(result.state.active)
    }

    @Test fun threeCorrectAttacksDefeatEnemyWithoutHeroDamage() {
        var state = engine.start("c1")
        repeat(2) {
            val result = engine.resolveMathAction(state, CombatAction.ATTACK, true)
            assertEquals(CombatResolution.HIT, result.resolution)
            state = result.state
        }
        val victory = engine.resolveMathAction(state, CombatAction.ATTACK, true)
        assertEquals(CombatResolution.VICTORY, victory.resolution)
        assertEquals(0, victory.state.enemyHp)
        assertEquals(3, victory.state.heroHearts)
        assertFalse(victory.state.active)
    }

    @Test fun victoryEmitsCombatEvent() {
        val state = engine.start("c1", heroHearts = 3, enemyHp = 1)
        val result = engine.resolveMathAction(state, CombatAction.ATTACK, true)
        assertEquals(CombatResolution.VICTORY, result.resolution)
        assertEquals("COMBAT_VICTORY", result.gameEventType)
        assertFalse(result.state.active)
    }

    @Test fun defeatPreservesConfirmedStateAndEndsCombat() {
        val state = engine.start("c1", heroHearts = 1, enemyHp = 3)
        val result = engine.resolveMathAction(state, CombatAction.ATTACK, false)
        assertEquals(CombatResolution.DEFEAT, result.resolution)
        assertEquals(0, result.state.heroHearts)
        assertFalse(result.state.active)
    }

    @Test fun fleeEndsCombatWithoutVictory() {
        val result = engine.resolveMathAction(engine.start("c1"), CombatAction.FLEE, false)
        assertEquals(CombatResolution.FLED, result.resolution)
        assertFalse(result.state.active)
        assertEquals(null, result.gameEventType)
    }
}

package com.ihorvoloshyn.mathadventure.ui.battle

import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class BattleUiStateTest {
    private val engine = CombatEngine()
    private val machine = BattleStateMachine()

    @Test
    fun successfulAttackProducesResolvedState() {
        val combat = engine.start("combat-1", heroHearts = 3, enemyHp = 3)
        val started = machine.attackStarted(machine.ready(combat), damage = 1)
        val outcome = engine.resolveMathAction(combat, com.mathadventure.core.combat.CombatAction.ATTACK, true, 1)
        val resolved = machine.resolve(started, outcome.state, outcome.resolution)

        assertTrue(resolved is BattleUiState.AttackResolved)
        assertEquals(CombatResolution.HIT, resolved.resolution)
        assertEquals(2, resolved.combat.enemyHp)
    }

    @Test
    fun victoryCanTransitionToRewarded() {
        val combat = engine.start("combat-1", heroHearts = 3, enemyHp = 1)
        val started = machine.attackStarted(machine.ready(combat), damage = 1)
        val outcome = engine.resolveMathAction(combat, com.mathadventure.core.combat.CombatAction.ATTACK, true, 1)
        val rewarded = machine.reward(machine.resolve(started, outcome.state, outcome.resolution))

        assertTrue(rewarded is BattleUiState.Rewarded)
        assertEquals(CombatResolution.VICTORY, rewarded.resolution)
    }
}

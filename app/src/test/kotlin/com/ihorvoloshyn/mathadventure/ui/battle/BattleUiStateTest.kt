package com.ihorvoloshyn.mathadventure.ui.battle

import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class BattleUiStateTest {
    private val engine = CombatEngine()
    private val machine = BattleStateMachine()

    @Test
    fun successfulAttackProducesResolvedState() {
        val combat = engine.start("combat-1", heroHearts = 3, enemyHp = 3)
        val started = machine.attackStarted(machine.ready(combat), damage = 1)
        val outcome = engine.resolveMathAction(combat, com.mathadventure.core.combat.CombatAction.ATTACK, true, 1)
        val resolved = machine.resolve(started, outcome.state, outcome.resolution)

        val resolvedState = assertInstanceOf(BattleUiState.AttackResolved::class.java, resolved)
        assertEquals(CombatResolution.HIT, resolvedState.resolution)
        assertEquals(2, resolvedState.combat.enemyHp)
    }

    @Test
    fun victoryCanTransitionToRewarded() {
        val combat = engine.start("combat-1", heroHearts = 3, enemyHp = 1)
        val started = machine.attackStarted(machine.ready(combat), damage = 1)
        val outcome = engine.resolveMathAction(combat, com.mathadventure.core.combat.CombatAction.ATTACK, true, 1)
        val rewarded = machine.reward(machine.resolve(started, outcome.state, outcome.resolution))

        val rewardedState = assertInstanceOf(BattleUiState.Rewarded::class.java, rewarded)
        assertEquals(CombatResolution.VICTORY, rewardedState.resolution)
    }
}

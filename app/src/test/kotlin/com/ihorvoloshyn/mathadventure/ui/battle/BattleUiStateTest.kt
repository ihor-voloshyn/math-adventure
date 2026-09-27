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
        val resolved = assertInstanceOf(
            BattleUiState.AttackResolved::class.java,
            machine.resolve(started, outcome.state, outcome.resolution)
        )
        assertEquals(CombatResolution.HIT, resolved.resolution)
        assertEquals(2, resolved.combat.enemyHp)
    }

    @Test
    fun victoryCanTransitionToRewarded() {
        val combat = engine.start("combat-1", heroHearts = 3, enemyHp = 1)
        val started = machine.attackStarted(machine.ready(combat), damage = 1)
        val outcome = engine.resolveMathAction(combat, com.mathadventure.core.combat.CombatAction.ATTACK, true, 1)
        val resolved = machine.resolve(started, outcome.state, outcome.resolution)
        val rewarded = assertInstanceOf(BattleUiState.Rewarded::class.java, machine.reward(resolved))
        assertEquals(CombatResolution.VICTORY, rewarded.resolution)
    }
}

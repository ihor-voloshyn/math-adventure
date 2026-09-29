package com.ihorvoloshyn.mathadventure.ui.battle

import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class BattleUiStateTest {
    private val engine = CombatEngine()
    private val machine = BattleStateMachine()

    @Test
    fun answerProducesResolvedState() {
        val combat = engine.start("combat-1")
        val ready = machine.ready(combat)
        val outcome = engine.resolveMathAnswer(combat, true)
        val resolved = machine.resolve(ready, outcome.state, outcome.resolution)

        assertTrue(resolved is BattleUiState.AnswerResolved)
        assertEquals(CombatResolution.VICTORY, resolved.resolution)
    }

    @Test
    fun resolvedStateCanBeRewarded() {
        val combat = engine.start("combat-1")
        val ready = machine.ready(combat)
        val outcome = engine.resolveMathAnswer(combat, true)
        val rewarded = machine.reward(machine.resolve(ready, outcome.state, outcome.resolution))

        assertTrue(rewarded is BattleUiState.Rewarded)
        assertEquals(CombatResolution.VICTORY, rewarded.resolution)
    }
}

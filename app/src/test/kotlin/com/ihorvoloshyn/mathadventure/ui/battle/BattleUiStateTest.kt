package com.ihorvoloshyn.mathadventure.ui.battle

import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BattleUiStateTest {
    private val engine = CombatEngine()
    private val machine = BattleStateMachine()

    @Test
    fun answerProducesResolvedState() {
        val combat = engine.start("combat-1")
        val ready = machine.ready(combat)
        val outcome = engine.resolveMathAnswer(combat, true)
        val resolved = machine.resolve(ready, outcome.state, outcome.resolution)

        assertIs<BattleUiState.AnswerResolved>(resolved)
        assertEquals(CombatResolution.VICTORY, resolved.resolution)
    }

    @Test
    fun resolvedStateCanBeRewarded() {
        val combat = engine.start("combat-1")
        val ready = machine.ready(combat)
        val outcome = engine.resolveMathAnswer(combat, true)
        val rewarded = machine.reward(machine.resolve(ready, outcome.state, outcome.resolution))

        assertIs<BattleUiState.Rewarded>(rewarded)
        assertEquals(CombatResolution.VICTORY, rewarded.resolution)
    }
}

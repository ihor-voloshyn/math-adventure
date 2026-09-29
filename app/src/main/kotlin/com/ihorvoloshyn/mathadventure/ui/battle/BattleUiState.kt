package com.ihorvoloshyn.mathadventure.ui.battle

import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.combat.CombatState

sealed interface BattleUiState {
    val combat: CombatState

    data class Ready(override val combat: CombatState) : BattleUiState

    data class AnswerResolved(
        override val combat: CombatState,
        val resolution: CombatResolution
    ) : BattleUiState

    data class Rewarded(
        override val combat: CombatState,
        val resolution: CombatResolution
    ) : BattleUiState
}

class BattleStateMachine {
    fun ready(combat: CombatState): BattleUiState = BattleUiState.Ready(combat)

    fun resolve(
        state: BattleUiState,
        combat: CombatState,
        resolution: CombatResolution
    ): BattleUiState.AnswerResolved =
        BattleUiState.AnswerResolved(combat, resolution)

    fun reward(state: BattleUiState.AnswerResolved): BattleUiState.Rewarded =
        BattleUiState.Rewarded(state.combat, state.resolution)
}

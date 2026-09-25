package com.ihorvoloshyn.mathadventure.ui.battle

import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.combat.CombatState

sealed interface BattleUiState {
    val combat: CombatState

    data class Ready(override val combat: CombatState) : BattleUiState
    data class AttackStarted(
        override val combat: CombatState,
        val damage: Int
    ) : BattleUiState
    data class AttackResolved(
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

    fun attackStarted(state: BattleUiState, damage: Int): BattleUiState.AttackStarted {
        require(damage > 0) { "damage must be positive" }
        return BattleUiState.AttackStarted(state.combat, damage)
    }

    fun resolve(state: BattleUiState.AttackStarted, combat: CombatState, resolution: CombatResolution): BattleUiState =
        BattleUiState.AttackResolved(combat, resolution)

    fun reward(state: BattleUiState.AttackResolved): BattleUiState.Rewarded =
        BattleUiState.Rewarded(state.combat, state.resolution)
}

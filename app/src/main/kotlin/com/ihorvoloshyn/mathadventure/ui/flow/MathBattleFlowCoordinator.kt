package com.ihorvoloshyn.mathadventure.ui.flow

import com.ihorvoloshyn.mathadventure.ui.battle.BattleStateMachine
import com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState
import com.ihorvoloshyn.mathadventure.ui.math.MathTaskStateMachine
import com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState
import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.combat.CombatState
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.flow.GeneratedTask
import com.mathadventure.core.model.AnswerResult

/**
 * Coordinates one quest = one math task.
 * There is no attack/defense/flee phase: every answer is the encounter action.
 */
class MathBattleFlowCoordinator(
    private val learningFlow: CoreLearningFlow,
    private val combatEngine: CombatEngine = CombatEngine(),
    private val mathStateMachine: MathTaskStateMachine = MathTaskStateMachine(),
    private val battleStateMachine: BattleStateMachine = BattleStateMachine()
) {
    var mathState: MathTaskUiState? = null
        private set

    var battleState: BattleUiState? = null
        private set

    fun startCombat(combat: CombatState) {
        battleState = battleStateMachine.ready(combat)
        mathState = null
    }

    fun presentTask(task: GeneratedTask, taskIndex: Int, totalTasks: Int) {
        mathState = mathStateMachine.present(task.task, taskIndex, totalTasks)
    }

    fun selectAnswer(answer: String) {
        mathState = mathStateMachine.select(
            mathState ?: error("No math task is presented"),
            answer
        )
    }

    fun evaluate(
        playerId: String,
        generated: GeneratedTask,
        attemptId: String,
        answer: String,
        timestampEpochMillis: Long,
        evidenceMetadata: Map<String, String>
    ): CombatResolution {
        selectAnswer(answer)
        val selected = mathState as MathTaskUiState.Selected
        val evaluation = learningFlow.answer(
            playerId = playerId,
            generated = generated,
            attemptId = attemptId,
            submittedAnswer = answer,
            timestampEpochMillis = timestampEpochMillis,
            evidenceMetadata = evidenceMetadata
        ).evaluation

        mathState = mathStateMachine.resolve(selected, evaluation)

        val currentBattle = battleState ?: error("Encounter has not started")
        val outcome = combatEngine.resolveMathAnswer(
            currentBattle.combat,
            evaluation.result == AnswerResult.CORRECT
        )
        battleState = battleStateMachine.resolve(
            currentBattle,
            outcome.state,
            outcome.resolution
        )
        return outcome.resolution
    }

    fun rewardCurrentBattle() {
        val resolved = battleState as? BattleUiState.AnswerResolved ?: return
        battleState = battleStateMachine.reward(resolved)
    }
}

package com.ihorvoloshyn.mathadventure.ui.flow

import com.ihorvoloshyn.mathadventure.ui.battle.BattleStateMachine
import com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState
import com.ihorvoloshyn.mathadventure.ui.math.MathTaskStateMachine
import com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState
import com.mathadventure.core.combat.CombatAction
import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.combat.CombatState
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.flow.GeneratedTask
import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.AnswerResult

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
        mathState = mathStateMachine.select(mathState ?: error("No math task is presented"), answer)
    }

    fun evaluate(
        playerId: String,
        generated: GeneratedTask,
        attemptId: String,
        answer: String,
        timestampEpochMillis: Long,
        evidenceMetadata: Map<String, String>,
        attackDamage: Int
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

        val battle = battleState ?: error("Combat has not started")
        val started = battleStateMachine.attackStarted(battle, attackDamage)
        battleState = started
        val outcome = combatEngine.resolveMathAction(
            battle.combat,
            CombatAction.ATTACK,
            evaluation.result == AnswerResult.CORRECT,
            attackDamage
        )
        battleState = battleStateMachine.resolve(started, outcome.state, outcome.resolution)
        return outcome.resolution
    }

    fun rewardCurrentBattle() {
        val resolved = battleState as? BattleUiState.AttackResolved ?: return
        battleState = battleStateMachine.reward(resolved)
    }
}

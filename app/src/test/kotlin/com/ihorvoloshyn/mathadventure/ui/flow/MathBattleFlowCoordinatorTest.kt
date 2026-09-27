package com.ihorvoloshyn.mathadventure.ui.flow

import com.mathadventure.core.adaptive.AdaptiveCandidate
import com.mathadventure.core.adaptive.AdaptivePolicy
import com.mathadventure.core.adaptive.AdaptivePriority
import com.mathadventure.core.adaptive.RuleBasedAdaptiveEngine
import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.mastery.ApprovedMasteryPolicy
import com.mathadventure.core.mastery.InMemoryMasteryStateStore
import com.mathadventure.core.mastery.PolicyDrivenMasterySystem
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode
import com.mathadventure.core.validation.LogicalTaskValidator
import com.mathadventure.core.validation.MathematicalTaskValidator
import com.mathadventure.core.validation.StructuralTaskValidator
import com.mathadventure.core.validation.TaskValidationPipeline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class MathBattleFlowCoordinatorTest {
    private val mastery = PolicyDrivenMasterySystem(InMemoryMasteryStateStore(), ApprovedMasteryPolicy())
    private val math = BasicMathEngine()
    private val flow = CoreLearningFlow(
        adaptive = RuleBasedAdaptiveEngine(Curriculum.mvp(), object : AdaptivePolicy {
            override fun priority(candidate: AdaptiveCandidate): AdaptivePriority? = AdaptivePriority.REINFORCE
            override fun mode(candidate: AdaptiveCandidate): TaskMode = TaskMode.DIRECT
            override fun difficulty(candidate: AdaptiveCandidate): Int = 1
            override fun contextType(candidate: AdaptiveCandidate): String = "BATTLE"
        }),
        generator = DeterministicTaskGenerator(),
        validation = TaskValidationPipeline(
            StructuralTaskValidator(), LogicalTaskValidator(), MathematicalTaskValidator(math)
        ),
        mathEngine = math,
        mastery = mastery
    )

    @Test
    fun correctAnswerProducesHitAndRewardableBattleState() {
        val coordinator = MathBattleFlowCoordinator(flow, CombatEngine())
        val combat = CombatEngine().start("test", heroHearts = 3, enemyHp = 3)
        coordinator.startCombat(combat)
        val generated = flow.generateNext(
            playerId = "player",
            skillStates = listOf(SkillState("ADD_BASIC", 0)),
            availableSkills = setOf("ADD_BASIC"),
            inputType = InputType.NUMERIC,
            generationContext = mapOf("taskIndex" to "0")
        )
        coordinator.presentTask(generated, 1, 3)
        val resolution = coordinator.evaluate("player", generated, "attempt-1", generated.task.answerSpec, 1L, emptyMap(), 1)
        assertEquals(CombatResolution.HIT, resolution)
        assertIs<com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Correct>(coordinator.mathState)
        assertIs<com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.AttackStarted>(coordinator.battleState)
        assertEquals(CombatResolution.HIT, coordinator.resolvePendingAttack())
        assertIs<com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.AttackResolved>(coordinator.battleState)
        coordinator.rewardCurrentBattle()
        assertIs<com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.Rewarded>(coordinator.battleState)
    }

    @Test
    fun pendingAttackCanBeResolvedOnlyOnce() {
        val coordinator = MathBattleFlowCoordinator(flow, CombatEngine())
        val combat = CombatEngine().start("test", heroHearts = 3, enemyHp = 3)
        coordinator.startCombat(combat)
        val generated = flow.generateNext(
            playerId = "player",
            skillStates = listOf(SkillState("ADD_BASIC", 0)),
            availableSkills = setOf("ADD_BASIC"),
            inputType = InputType.NUMERIC,
            generationContext = mapOf("taskIndex" to "0")
        )
        coordinator.presentTask(generated, 1, 3)
        coordinator.evaluate("player", generated, "attempt-once", generated.task.answerSpec, 1L, emptyMap(), 1)

        assertEquals(CombatResolution.HIT, coordinator.resolvePendingAttack())
        assertFailsWith<IllegalStateException> {
            coordinator.resolvePendingAttack()
        }
    }

    @Test
    fun incorrectAnswerDoesNotProduceHit() {
        val coordinator = MathBattleFlowCoordinator(flow, CombatEngine())
        val combat = CombatEngine().start("test", heroHearts = 3, enemyHp = 3)
        coordinator.startCombat(combat)
        val generated = flow.generateNext(
            playerId = "player",
            skillStates = listOf(SkillState("ADD_BASIC", 0)),
            availableSkills = setOf("ADD_BASIC"),
            inputType = InputType.NUMERIC,
            generationContext = mapOf("taskIndex" to "0")
        )
        coordinator.presentTask(generated, 1, 3)
        val wrong = (generated.task.answerSpec.toInt() + 1).toString()
        val resolution = coordinator.evaluate("player", generated, "attempt-2", wrong, 2L, emptyMap(), 1)
        assertEquals(CombatResolution.MISS, resolution)
        assertIs<com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Incorrect>(coordinator.mathState)
    }
}

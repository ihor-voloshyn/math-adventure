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
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

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
        val combat = CombatEngine().start("test")
        coordinator.startCombat(combat)
        val generated = flow.generateNext(
            playerId = "player",
            skillStates = listOf(SkillState("ADD_BASIC", 0)),
            availableSkills = setOf("ADD_BASIC"),
            inputType = InputType.NUMERIC,
            generationContext = mapOf("taskIndex" to "0")
        )
        coordinator.presentTask(generated, 1, 3)
        val resolution = coordinator.evaluate("player", generated, "attempt-1", generated.task.answerSpec, 1L, emptyMap())
        assertEquals(CombatResolution.VICTORY, resolution)
        assertTrue(coordinator.mathState is com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Correct)
        assertTrue(coordinator.battleState is com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.AnswerResolved)
        assertEquals(CombatResolution.VICTORY, (coordinator.battleState as com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.AnswerResolved).resolution)
        coordinator.rewardCurrentBattle()
        assertTrue(coordinator.battleState is com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.Rewarded)
    }

    @Test
    fun incorrectAnswerConsumesOneHeartAndKeepsTaskActive() {
        val coordinator = MathBattleFlowCoordinator(flow, CombatEngine())
        val combat = CombatEngine().start("test")
        coordinator.startCombat(combat)
        val generated = flow.generateNext(
            playerId = "player",
            skillStates = listOf(SkillState("ADD_BASIC", 0)),
            availableSkills = setOf("ADD_BASIC"),
            inputType = InputType.NUMERIC,
            generationContext = mapOf("taskIndex" to "0")
        )
        coordinator.presentTask(generated, 1, 1)
        val wrong = (generated.task.answerSpec.toInt() + 1).toString()
        val resolution = coordinator.evaluate("player", generated, "attempt-2", wrong, 2L, emptyMap())
        assertEquals(CombatResolution.INCORRECT, resolution)
        assertEquals(2, coordinator.battleState!!.combat.heroHearts)
        assertTrue(coordinator.mathState is com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Incorrect)
    }

    @Test
    fun incorrectAnswerDoesNotProduceHit() {
        val coordinator = MathBattleFlowCoordinator(flow, CombatEngine())
        val combat = CombatEngine().start("test")
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
        val resolution = coordinator.evaluate("player", generated, "attempt-2", wrong, 2L, emptyMap())
        assertEquals(CombatResolution.INCORRECT, resolution)
        assertTrue(coordinator.mathState is com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Incorrect)
    }
}

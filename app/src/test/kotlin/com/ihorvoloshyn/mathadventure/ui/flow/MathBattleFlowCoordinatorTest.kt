package com.ihorvoloshyn.mathadventure.ui.flow

import com.mathadventure.core.adaptive.*
import com.mathadventure.core.combat.*
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.mastery.*
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.*
import com.mathadventure.core.validation.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MathBattleFlowCoordinatorTest {
    private val mastery = PolicyDrivenMasterySystem(InMemoryMasteryStateStore(), ApprovedMasteryPolicy())
    private val math = BasicMathEngine()
    private val flow = CoreLearningFlow(
        adaptive = RuleBasedAdaptiveEngine(
            Curriculum.mvp(),
            object : AdaptivePolicy {
                override fun priority(candidate: AdaptiveCandidate) = AdaptivePriority.REINFORCE
                override fun mode(candidate: AdaptiveCandidate) = TaskMode.DIRECT
                override fun difficulty(candidate: AdaptiveCandidate) = 1
                override fun contextType(candidate: AdaptiveCandidate) = "BATTLE"
            }
        ),
        generator = DeterministicTaskGenerator(),
        validation = TaskValidationPipeline(
            StructuralTaskValidator(), LogicalTaskValidator(), MathematicalTaskValidator(math)
        ),
        mathEngine = math,
        mastery = mastery
    )

    @Test
    fun correctAnswerProducesHitAndRewardableBattleState() {
        val c = MathBattleFlowCoordinator(flow, CombatEngine())
        c.startCombat(CombatEngine().start("test", 3, 3))
        val g = flow.generateNext(
            "player", listOf(SkillState("ADD_BASIC", 0)), setOf("ADD_BASIC"),
            InputType.NUMERIC, mapOf("taskIndex" to "0")
        )
        c.presentTask(g, 1, 3)

        assertEquals(
            CombatResolution.HIT,
            c.evaluate("player", g, "attempt-1", g.task.answerSpec, 1L, emptyMap(), 1)
        )
        assertTrue(c.mathState is com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Correct)
        assertTrue(c.battleState is com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.AttackStarted)
        assertEquals(CombatResolution.HIT, c.resolvePendingAttack())
        assertTrue(c.battleState is com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.AttackResolved)
        c.rewardCurrentBattle()
        assertTrue(c.battleState is com.ihorvoloshyn.mathadventure.ui.battle.BattleUiState.Rewarded)
    }

    @Test
    fun pendingAttackCanBeResolvedOnlyOnce() {
        val c = MathBattleFlowCoordinator(flow, CombatEngine())
        c.startCombat(CombatEngine().start("test", 3, 3))
        val g = flow.generateNext(
            "player", listOf(SkillState("ADD_BASIC", 0)), setOf("ADD_BASIC"),
            InputType.NUMERIC, mapOf("taskIndex" to "0")
        )
        c.presentTask(g, 1, 3)
        c.evaluate("player", g, "attempt-once", g.task.answerSpec, 1L, emptyMap(), 1)
        assertEquals(CombatResolution.HIT, c.resolvePendingAttack())

        try {
            c.resolvePendingAttack()
            throw AssertionError("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
            // expected
        }
    }

    @Test
    fun incorrectAnswerDoesNotProduceHit() {
        val c = MathBattleFlowCoordinator(flow, CombatEngine())
        c.startCombat(CombatEngine().start("test", 3, 3))
        val g = flow.generateNext(
            "player", listOf(SkillState("ADD_BASIC", 0)), setOf("ADD_BASIC"),
            InputType.NUMERIC, mapOf("taskIndex" to "0")
        )
        c.presentTask(g, 1, 3)
        val wrong = (g.task.answerSpec.toInt() + 1).toString()

        assertEquals(
            CombatResolution.MISS,
            c.evaluate("player", g, "attempt-2", wrong, 2L, emptyMap(), 1)
        )
        assertTrue(c.mathState is com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState.Incorrect)
    }
}

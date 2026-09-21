package com.mathadventure.core.adaptive

import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode
import kotlin.test.Test
import kotlin.test.assertEquals

class RuleBasedAdaptiveEngineTest {
    private val curriculum = Curriculum.mvp()

    private val policy = object : AdaptivePolicy {
        override fun priority(candidate: AdaptiveCandidate): AdaptivePriority? =
            when {
                candidate.state.consecutiveErrors >= 3 -> AdaptivePriority.CRITICAL_REMEDIATION
                !candidate.requiredPrerequisitesSatisfied -> AdaptivePriority.REQUIRED_PREREQUISITE
                candidate.state.recentErrors > 0 -> AdaptivePriority.RECENT_ERROR_RECOVERY
                candidate.state.reviewState == "DUE_REVIEW" -> AdaptivePriority.DUE_REVIEW
                candidate.state.mastery < 4 -> AdaptivePriority.REINFORCE
                candidate.state.mastery >= 4 -> AdaptivePriority.ADVANCE
                else -> null
            }

        override fun mode(candidate: AdaptiveCandidate): TaskMode =
            when (priority(candidate)) {
                AdaptivePriority.CRITICAL_REMEDIATION -> TaskMode.REPRESENTATION
                AdaptivePriority.REQUIRED_PREREQUISITE,
                AdaptivePriority.RECENT_ERROR_RECOVERY,
                AdaptivePriority.REINFORCE,
                AdaptivePriority.ADVANCE -> TaskMode.DIRECT
                AdaptivePriority.DUE_REVIEW -> TaskMode.REPRESENTATION
                null -> TaskMode.DIRECT
            }

        override fun difficulty(candidate: AdaptiveCandidate): Int =
            when (priority(candidate)) {
                AdaptivePriority.CRITICAL_REMEDIATION,
                AdaptivePriority.REQUIRED_PREREQUISITE -> 1
                AdaptivePriority.RECENT_ERROR_RECOVERY,
                AdaptivePriority.DUE_REVIEW,
                AdaptivePriority.REINFORCE -> maxOf(1, candidate.state.mastery)
                AdaptivePriority.ADVANCE -> maxOf(1, candidate.state.mastery + 1)
                null -> 1
            }

        override fun contextType(candidate: AdaptiveCandidate): String = "TEST"
    }

    @Test
    fun criticalRemediationHasPriorityOverAdvance() {
        val engine = RuleBasedAdaptiveEngine(curriculum, policy)
        val decision = engine.decideNext(
            "p1",
            listOf(
                SkillState("ADD_BASIC", mastery = 5),
                SkillState("SUB_BASIC", mastery = 3, consecutiveErrors = 3)
            ),
            setOf("ADD_BASIC", "SUB_BASIC")
        )
        assertEquals("SUB_BASIC", decision.skillId)
        assertEquals("CRITICAL_REMEDIATION", decision.reason)
    }

    @Test
    fun unmetPrerequisiteIsSelectedBeforeAdvance() {
        val engine = RuleBasedAdaptiveEngine(curriculum, policy)
        val decision = engine.decideNext(
            "p1",
            listOf(
                SkillState("NUM_COMPARE", mastery = 3),
                SkillState("ADD_BASIC", mastery = 3)
            ),
            setOf("ADD_CROSS_TEN", "ADD_BASIC")
        )
        assertEquals("ADD_CROSS_TEN", decision.skillId)
        assertEquals("REQUIRED_PREREQUISITE", decision.reason)
    }

    @Test
    fun decisionIsDeterministicForSameInputs() {
        val engine = RuleBasedAdaptiveEngine(curriculum, policy)
        val states = listOf(SkillState("ADD_BASIC", mastery = 4))
        val first = engine.decideNext("p1", states, setOf("ADD_BASIC"))
        val second = engine.decideNext("p1", states, setOf("ADD_BASIC"))
        assertEquals(first, second)
    }
}

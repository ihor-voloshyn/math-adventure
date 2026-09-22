package com.mathadventure.core.mastery

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.MasteryUpdateInput
import com.mathadventure.core.model.SkillState
import kotlin.test.Test
import kotlin.test.assertEquals

class ApprovedMasteryPolicyTest {
    private val policy = ApprovedMasteryPolicy()
    private fun input(result: AnswerResult, metadata: Map<String,String> = emptyMap()) =
        MasteryUpdateInput("p", "ADD_BASIC", "a", "t", 1L, result, false, null, 2, "BATTLE", metadata)

    @Test fun singleCorrectDoesNotJumpToFive() {
        val state = policy.update(SkillState("ADD_BASIC", 0), input(AnswerResult.CORRECT))
        assertEquals(1, state.mastery)
    }

    @Test fun singleErrorDoesNotResetMastery() {
        val state = policy.update(SkillState("ADD_BASIC", 4), input(AnswerResult.INCORRECT))
        assertEquals(4, state.mastery)
    }

    @Test fun skippedIsNeutral() {
        val state = policy.update(SkillState("ADD_BASIC", 3), input(AnswerResult.SKIPPED))
        assertEquals(3, state.mastery)
    }

    @Test fun repeatedErrorsCauseControlledDemotion() {
        var state = SkillState("ADD_BASIC", 4)
        repeat(3) { state = policy.update(state, input(AnswerResult.INCORRECT)) }
        assertEquals(3, state.mastery)
    }

    @Test fun masteryFourRequiresDiverseEvidence() {
        var state = SkillState("ADD_BASIC", 3)
        repeat(4) { state = policy.update(state, input(AnswerResult.CORRECT, mapOf("difficultyBand" to "TARGET"))) }
        assertEquals(3, state.mastery)
        state = policy.update(state, input(AnswerResult.CORRECT, mapOf("difficultyBand" to "TARGET", "evidenceDiverse" to "true")))
        assertEquals(4, state.mastery)
    }

    @Test fun masteryFiveRequiresDelayedReview() {
        var state = SkillState("ADD_BASIC", 4)
        repeat(3) { state = policy.update(state, input(AnswerResult.CORRECT, mapOf("difficultyBand" to "TARGET", "evidenceDiverse" to "true"))) }
        assertEquals(4, state.mastery)
        state = policy.update(state, input(AnswerResult.CORRECT, mapOf("difficultyBand" to "TARGET", "evidenceDiverse" to "true", "delayedReview" to "true")))
        assertEquals(5, state.mastery)
    }
}

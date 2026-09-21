package com.mathadventure.core.mastery

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.MasteryUpdateInput
import com.mathadventure.core.model.SkillState
import kotlin.test.Test
import kotlin.test.assertEquals

class PolicyDrivenMasterySystemTest {
    @Test
    fun storesPolicyResultAndReadsItBack() {
        val store = InMemoryMasteryStateStore()
        val system = PolicyDrivenMasterySystem(store) { current, input ->
            current.copy(
                mastery = if (input.answerResult == AnswerResult.CORRECT) current.mastery + 1 else current.mastery,
                attempts = current.attempts + 1
            )
        }

        val input = MasteryUpdateInput(
            playerId = "p1",
            skillId = "ADD_BASIC",
            attemptId = "a1",
            taskId = "t1",
            timestampEpochMillis = 1L,
            answerResult = AnswerResult.CORRECT,
            hintUsed = false,
            hintLevel = null,
            difficulty = 1,
            contextType = "DIRECT"
        )

        val updated = system.applyValidatedAttempt(input)

        assertEquals(1, updated.mastery)
        assertEquals(updated, system.getSkillState("p1", "ADD_BASIC"))
    }

    @Test
    fun initialStateIsZeroWithoutInventingProgress() {
        val system = PolicyDrivenMasterySystem(
            InMemoryMasteryStateStore(),
            MasteryUpdatePolicy { current, _ -> current }
        )

        assertEquals(
            SkillState(skillId = "ADD_BASIC", mastery = 0),
            system.getSkillState("p1", "ADD_BASIC")
        )
    }
}

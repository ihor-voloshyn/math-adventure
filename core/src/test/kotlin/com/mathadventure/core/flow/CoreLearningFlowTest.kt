package com.mathadventure.core.flow

import com.mathadventure.core.adaptive.AdaptivePolicy
import com.mathadventure.core.adaptive.RuleBasedAdaptiveEngine
import com.mathadventure.core.contracts.ValidationResult
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.mastery.InMemoryMasteryStateStore
import com.mathadventure.core.mastery.MasteryUpdatePolicy
import com.mathadventure.core.mastery.PolicyDrivenMasterySystem
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode
import com.mathadventure.core.validation.LogicalTaskValidator
import com.mathadventure.core.validation.MathematicalTaskValidator
import com.mathadventure.core.validation.StructuralTaskValidator
import com.mathadventure.core.validation.TaskValidationPipeline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CoreLearningFlowTest {
    private val curriculum = Curriculum.mvp()

    private val adaptivePolicy = object : AdaptivePolicy {
        override fun priority(candidate: com.mathadventure.core.adaptive.AdaptiveCandidate) =
            when {
                !candidate.requiredPrerequisitesSatisfied -> com.mathadventure.core.adaptive.AdaptivePriority.REQUIRED_PREREQUISITE
                candidate.state.mastery < 4 -> com.mathadventure.core.adaptive.AdaptivePriority.REINFORCE
                else -> com.mathadventure.core.adaptive.AdaptivePriority.ADVANCE
            }

        override fun mode(candidate: com.mathadventure.core.adaptive.AdaptiveCandidate) = TaskMode.DIRECT
        override fun difficulty(candidate: com.mathadventure.core.adaptive.AdaptiveCandidate) = 1
        override fun contextType(candidate: com.mathadventure.core.adaptive.AdaptiveCandidate) = "TEST"
    }

    private fun flow(): CoreLearningFlow {
        val math = BasicMathEngine()
        val mastery = PolicyDrivenMasterySystem(
            InMemoryMasteryStateStore(),
            MasteryUpdatePolicy { current, input ->
                if (input.answerResult == AnswerResult.CORRECT) {
                    current.copy(
                        mastery = minOf(5, current.mastery + 1),
                        attempts = current.attempts + 1,
                        correctAttempts = current.correctAttempts + 1
                    )
                } else {
                    current.copy(
                        attempts = current.attempts + 1,
                        incorrectAttempts = current.incorrectAttempts + 1
                    )
                }
            }
        )
        return CoreLearningFlow(
            adaptive = RuleBasedAdaptiveEngine(curriculum, adaptivePolicy),
            generator = DeterministicTaskGenerator(),
            validation = TaskValidationPipeline(
                StructuralTaskValidator(),
                LogicalTaskValidator(),
                MathematicalTaskValidator(math)
            ),
            mathEngine = math,
            mastery = mastery
        )
    }

    @Test
    fun correctAnswerFlowsFromAdaptiveSelectionToMastery() {
        val flow = flow()
        val generated = flow.generateNext(
            playerId = "p1",
            skillStates = listOf(
                SkillState("NUM_COMPARE", mastery = 3),
                SkillState("ADD_BASIC", mastery = 0)
            ),
            availableSkills = setOf("ADD_BASIC"),
            inputType = InputType.NUMERIC
        )

        assertEquals("ADD_BASIC", generated.decision.skillId)
        assertEquals("12 + 5 = ?", generated.task.prompt)
        assertIs<ValidationResult.Valid>(
            TaskValidationPipeline(
                StructuralTaskValidator(),
                LogicalTaskValidator(),
                MathematicalTaskValidator(BasicMathEngine())
            ).validate(generated.task)
        )

        val answered = flow.answer(
            playerId = "p1",
            generated = generated,
            attemptId = "attempt-1",
            submittedAnswer = "17",
            timestampEpochMillis = 1L
        )

        assertTrue(flow.isCorrect(answered))
        assertEquals(1, answered.mastery.mastery)
        assertEquals(1, answered.mastery.attempts)
        assertEquals(1, answered.mastery.correctAttempts)
    }

    @Test
    fun incorrectAnswerStillProducesValidatedMasteryEvidence() {
        val flow = flow()
        val generated = flow.generateNext(
            "p1",
            listOf(SkillState("NUM_COMPARE", mastery = 3)),
            setOf("ADD_BASIC"),
            InputType.NUMERIC
        )

        val answered = flow.answer(
            "p1",
            generated,
            "attempt-2",
            "18",
            2L
        )

        assertEquals(AnswerResult.INCORRECT, answered.evaluation.result)
        assertEquals(1, answered.mastery.attempts)
        assertEquals(1, answered.mastery.incorrectAttempts)
        assertEquals("ADD_BASIC", answered.mastery.skillId)
        assertEquals(0, answered.mastery.mastery)
    }
}

package com.mathadventure.core.progression

import com.mathadventure.core.contracts.AdaptiveEngine
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.flow.GeneratedTask
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.mastery.InMemoryMasteryStateStore
import com.mathadventure.core.mastery.MasteryUpdatePolicy
import com.mathadventure.core.mastery.PolicyDrivenMasterySystem
import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.AdaptiveDecision
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode
import com.mathadventure.core.validation.LogicalTaskValidator
import com.mathadventure.core.validation.MathematicalTaskValidator
import com.mathadventure.core.validation.StructuralTaskValidator
import com.mathadventure.core.validation.TaskValidationPipeline

class CoreProgressionFlowTest {
    fun correctAnswerProducesOneAtomicProgressionCommit() {
        val skillId = Curriculum.mvp().skills.first().id
        val adaptive = object : AdaptiveEngine {
            override fun decideNext(
                playerId: String,
                skillStates: List<SkillState>,
                availableSkills: Set<String>
            ): AdaptiveDecision = AdaptiveDecision(
                skillId = skillId,
                mode = TaskMode.DIRECT,
                difficulty = 1,
                contextType = "practice",
                constraints = mapOf("operation" to "add"),
                reason = "test"
            )
        }

        val mastery = PolicyDrivenMasterySystem(
            InMemoryMasteryStateStore(),
            MasteryUpdatePolicy { current, input ->
                current.copy(
                    mastery = if (input.answerResult == AnswerResult.CORRECT) current.mastery + 1 else current.mastery,
                    attempts = current.attempts + 1
                )
            }
        )

        val mathEngine = BasicMathEngine()
        val learning = CoreLearningFlow(
            adaptive = adaptive,
            generator = DeterministicTaskGenerator(),
            validation = TaskValidationPipeline(
                structural = StructuralTaskValidator(),
                logical = LogicalTaskValidator(),
                mathematical = MathematicalTaskValidator(mathEngine)
            ),
            mathEngine = mathEngine,
            mastery = mastery
        )

        val store = RecordingProgressionStore()
        val rewardPolicy = object : RewardPolicy {
            override fun calculate(
                generated: GeneratedTask,
                evaluation: AttemptEvaluation,
                mastery: SkillState
            ): RewardDecision = RewardDecision(
                coinsDelta = if (evaluation.result == AnswerResult.CORRECT) 10L else 0L,
                reason = "test reward policy"
            )
        }

        val flow = CoreProgressionFlow(learning, rewardPolicy, store)
        val generated = learning.generateNext(
            playerId = "player-1",
            skillStates = emptyList(),
            availableSkills = setOf(skillId),
            inputType = InputType.NUMERIC
        )

        val commit = flow.answerAndCommit(
            playerId = "player-1",
            generated = generated,
            attemptId = "attempt-1",
            submittedAnswer = "17",
            timestampEpochMillis = 1_000L
        )

        check(commit.evaluation.result == AnswerResult.CORRECT)
        check(commit.skillId == skillId)
        check(commit.mastery.mastery == 1)
        check(commit.reward.coinsDelta == 10L)
        check(store.commits == listOf(commit))
    }

    private class RecordingProgressionStore : ProgressionStore {
        val commits = mutableListOf<ProgressionCommit>()

        override fun commit(progress: ProgressionCommit) {
            commits += progress
        }
    }
}

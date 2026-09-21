package com.mathadventure.core.flow

import com.mathadventure.core.contracts.AdaptiveEngine
import com.mathadventure.core.contracts.MasterySystem
import com.mathadventure.core.contracts.ValidationResult
import com.mathadventure.core.contracts.TaskGenerator
import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.math.MathEngine
import com.mathadventure.core.model.AdaptiveDecision
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.MasteryUpdateInput
import com.mathadventure.core.model.TaskBlueprint
import com.mathadventure.core.model.TaskInstance
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.validation.TaskValidationPipeline

data class GeneratedTask(
    val decision: AdaptiveDecision,
    val task: TaskInstance
)

data class AnsweredTask(
    val generated: GeneratedTask,
    val evaluation: AttemptEvaluation,
    val mastery: SkillState
)

class CoreLearningFlow(
    private val adaptive: AdaptiveEngine,
    private val generator: TaskGenerator,
    private val validation: TaskValidationPipeline,
    private val mathEngine: MathEngine,
    private val mastery: MasterySystem
) {
    fun generateNext(
        playerId: String,
        skillStates: List<SkillState>,
        availableSkills: Set<String>,
        inputType: InputType
    ): GeneratedTask {
        val decision = adaptive.decideNext(playerId, skillStates, availableSkills)
        val blueprint = TaskBlueprint(
            skillId = decision.skillId,
            mode = decision.mode,
            difficulty = decision.difficulty,
            contextType = decision.contextType,
            inputType = inputType,
            constraints = decision.constraints
        )
        val task = generator.generate(blueprint)
        check(validation.validate(task) is ValidationResult.Valid) {
            "Generated task failed validation: taskId=" + task.taskId
        }
        return GeneratedTask(decision, task)
    }

    fun answer(
        playerId: String,
        generated: GeneratedTask,
        attemptId: String,
        submittedAnswer: String,
        timestampEpochMillis: Long,
        hintUsed: Boolean = false,
        hintLevel: Int = 0,
        evidenceMetadata: Map<String, String> = emptyMap()
    ): AnsweredTask {
        val evaluation = mathEngine.evaluateAnswer(generated.task, submittedAnswer)
        val masteryState = mastery.applyValidatedAttempt(
            MasteryUpdateInput(
                playerId = playerId,
                skillId = generated.task.skillId,
                attemptId = attemptId,
                taskId = generated.task.taskId,
                timestampEpochMillis = timestampEpochMillis,
                answerResult = evaluation.result,
                hintUsed = hintUsed,
                hintLevel = hintLevel,
                difficulty = generated.task.difficulty,
                contextType = generated.task.contextType,
                evidenceMetadata = evidenceMetadata + evaluation.evidence
            )
        )
        return AnsweredTask(generated, evaluation, masteryState)
    }

    fun isCorrect(answered: AnsweredTask): Boolean =
        answered.evaluation.result == AnswerResult.CORRECT
}

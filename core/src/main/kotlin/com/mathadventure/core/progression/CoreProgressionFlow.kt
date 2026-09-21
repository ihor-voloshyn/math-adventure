package com.mathadventure.core.progression

import com.mathadventure.core.flow.AnsweredTask
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.flow.GeneratedTask

class CoreProgressionFlow(
    private val learningFlow: CoreLearningFlow,
    private val rewardPolicy: RewardPolicy,
    private val progressionStore: ProgressionStore
) {
    fun answerAndCommit(
        playerId: String,
        generated: GeneratedTask,
        attemptId: String,
        submittedAnswer: String,
        timestampEpochMillis: Long,
        hintUsed: Boolean = false,
        hintLevel: Int = 0,
        evidenceMetadata: Map<String, String> = emptyMap()
    ): ProgressionCommit {
        val answered: AnsweredTask = learningFlow.answer(
            playerId = playerId,
            generated = generated,
            attemptId = attemptId,
            submittedAnswer = submittedAnswer,
            timestampEpochMillis = timestampEpochMillis,
            hintUsed = hintUsed,
            hintLevel = hintLevel,
            evidenceMetadata = evidenceMetadata
        )

        val reward = rewardPolicy.calculate(
            generated = generated,
            evaluation = answered.evaluation,
            mastery = answered.mastery
        )

        val commit = ProgressionCommit(
            playerId = playerId,
            attemptId = attemptId,
            taskId = generated.task.taskId,
            skillId = generated.task.skillId,
            evaluation = answered.evaluation,
            mastery = answered.mastery,
            reward = reward
        )

        progressionStore.commit(commit)
        return commit
    }
}

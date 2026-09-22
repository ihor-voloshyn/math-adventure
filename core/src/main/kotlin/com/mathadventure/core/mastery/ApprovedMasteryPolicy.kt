package com.mathadventure.core.mastery

import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.MasteryUpdateInput
import com.mathadventure.core.model.SkillState

class ApprovedMasteryPolicy(
    private val config: MasteryPolicyConfig = MasteryPolicyConfig()
) : MasteryUpdatePolicy {
    override fun update(current: SkillState, input: MasteryUpdateInput): SkillState {
        val attempts = current.attempts + 1
        val now = input.timestampEpochMillis
        return when (input.answerResult) {
            AnswerResult.SKIPPED -> current.copy(attempts = attempts, lastAttemptAtEpochMillis = now)
            AnswerResult.INCORRECT -> {
                val errors = current.consecutiveErrors + 1
                val demote = errors >= config.consecutiveErrorsForDemotion && current.mastery > config.minimumMastery
                current.copy(
                    mastery = if (demote) current.mastery - 1 else current.mastery,
                    attempts = attempts,
                    incorrectAttempts = current.incorrectAttempts + 1,
                    recentErrors = current.recentErrors + 1,
                    lastAttemptAtEpochMillis = now,
                    consecutiveCorrect = 0,
                    consecutiveErrors = errors,
                    reviewState = if (demote) "REVIEW" else current.reviewState
                )
            }
            AnswerResult.CORRECT -> {
                val correct = current.correctAttempts + 1
                val streak = current.consecutiveCorrect + 1
                val evidence = Evidence(input.evidenceMetadata)
                val next = when (current.mastery) {
                    0 -> if (streak >= config.mastery1Successes) 1 else 0
                    1 -> if (streak >= config.mastery2Successes) 2 else 1
                    2 -> if (streak >= config.mastery3Successes && evidence.isNormalDifficulty) 3 else 2
                    3 -> if (streak >= config.mastery4Successes && evidence.isDiverse) 4 else 3
                    4 -> if (streak >= config.mastery5Successes && evidence.isDiverse && evidence.hasDelayedOrReviewEvidence) 5 else 4
                    5 -> 5
                    else -> current.mastery
                }
                current.copy(
                    mastery = next,
                    attempts = attempts,
                    correctAttempts = correct,
                    lastAttemptAtEpochMillis = now,
                    lastCorrectAtEpochMillis = now,
                    consecutiveCorrect = streak,
                    consecutiveErrors = 0,
                    reviewState = if (next >= 4) "STABLE" else current.reviewState
                )
            }
        }
    }
}

data class MasteryPolicyConfig(
    val mastery1Successes: Int = 1,
    val mastery2Successes: Int = 2,
    val mastery3Successes: Int = 3,
    val mastery4Successes: Int = 4,
    val mastery5Successes: Int = 3,
    val consecutiveErrorsForDemotion: Int = 3,
    val minimumMastery: Int = 1
) {
    init {
        require(mastery1Successes >= 1)
        require(mastery2Successes >= mastery1Successes)
        require(mastery3Successes >= mastery2Successes)
        require(mastery4Successes >= mastery3Successes)
        require(mastery5Successes >= 1)
        require(consecutiveErrorsForDemotion >= 2)
        require(minimumMastery in 0..5)
    }
}

private class Evidence(metadata: Map<String, String>) {
    val isNormalDifficulty = metadata["difficultyBand"] == "NORMAL" || metadata["difficultyBand"] == "TARGET"
    val isDiverse = metadata["evidenceDiverse"] == "true"
    val hasDelayedOrReviewEvidence = metadata["delayedReview"] == "true" || metadata["review"] == "true"
}

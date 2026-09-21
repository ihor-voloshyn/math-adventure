package com.mathadventure.core.model

enum class AnswerResult {
    CORRECT,
    INCORRECT,
    SKIPPED
}

data class MasteryUpdateInput(
    val playerId: String,
    val skillId: String,
    val attemptId: String,
    val taskId: String,
    val timestampEpochMillis: Long,
    val answerResult: AnswerResult,
    val hintUsed: Boolean,
    val hintLevel: Int?,
    val difficulty: Int,
    val contextType: String,
    val evidenceMetadata: Map<String, String> = emptyMap()
)

data class SkillState(
    val skillId: String,
    val mastery: Int,
    val attempts: Int = 0,
    val correctAttempts: Int = 0,
    val incorrectAttempts: Int = 0,
    val recentErrors: Int = 0,
    val lastAttemptAtEpochMillis: Long? = null,
    val lastCorrectAtEpochMillis: Long? = null,
    val consecutiveCorrect: Int = 0,
    val consecutiveErrors: Int = 0,
    val reviewState: String? = null
) {
    init {
        require(mastery in 0..5) { "mastery must be between 0 and 5" }
    }
}

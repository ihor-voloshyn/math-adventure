package com.mathadventure.core.progression

import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.SkillState

data class RewardDecision(
    val coinsDelta: Long,
    val reason: String
)

data class ProgressionCommit(
    val playerId: String,
    val attemptId: String,
    val taskId: String,
    val skillId: String,
    val evaluation: AttemptEvaluation,
    val mastery: SkillState,
    val reward: RewardDecision
)

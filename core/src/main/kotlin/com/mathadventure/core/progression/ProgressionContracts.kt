package com.mathadventure.core.progression

import com.mathadventure.core.flow.GeneratedTask
import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.SkillState

interface RewardPolicy {
    fun calculate(
        generated: GeneratedTask,
        evaluation: AttemptEvaluation,
        mastery: SkillState
    ): RewardDecision
}

interface ProgressionStore {
    /**
     * Commits the validated learning result and all derived consequences
     * as one durable unit.
     *
     * The persistence implementation owns transaction/recovery mechanics.
     * It does not decide mathematical correctness, Mastery policy, or reward policy.
     */
    fun commit(progress: ProgressionCommit)
}

package com.mathadventure.core.mastery

import com.mathadventure.core.contracts.MasterySystem
import com.mathadventure.core.model.MasteryUpdateInput
import com.mathadventure.core.model.SkillState

class PolicyDrivenMasterySystem(
    private val store: MasteryStateStore,
    private val policy: MasteryUpdatePolicy
) : MasterySystem {

    override fun getSkillState(playerId: String, skillId: String): SkillState =
        store.get(playerId, skillId) ?: SkillState(skillId = skillId, mastery = 0)

    override fun applyValidatedAttempt(input: MasteryUpdateInput): SkillState {
        require(input.playerId.isNotBlank()) { "playerId is required" }
        require(input.skillId.isNotBlank()) { "skillId is required" }
        require(input.attemptId.isNotBlank()) { "attemptId is required" }
        require(input.taskId.isNotBlank()) { "taskId is required" }
        require(input.difficulty >= 1) { "difficulty must be positive" }

        val current = getSkillState(input.playerId, input.skillId)
        val updated = policy.update(current, input)
        require(updated.skillId == input.skillId) { "Mastery policy cannot change skillId" }
        require(updated.mastery in 0..5) { "Mastery policy must keep mastery between 0 and 5" }

        store.put(input.playerId, updated)
        return updated
    }
}

package com.mathadventure.core.adaptive

import com.mathadventure.core.contracts.AdaptiveEngine
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.model.AdaptiveDecision
import com.mathadventure.core.model.SkillState

class RuleBasedAdaptiveEngine(
    private val curriculum: Curriculum,
    private val policy: AdaptivePolicy
) : AdaptiveEngine {

    override fun decideNext(
        playerId: String,
        skillStates: List<SkillState>,
        availableSkills: Set<String>
    ): AdaptiveDecision {
        require(playerId.isNotBlank()) { "playerId is required" }
        require(availableSkills.isNotEmpty()) { "availableSkills must not be empty" }

        val statesById = skillStates.associateBy { it.skillId }
        val candidates = availableSkills.map { skillId ->
            val state = statesById[skillId] ?: SkillState(skillId, 0)
            AdaptiveCandidate(
                skillId = skillId,
                state = state,
                requiredPrerequisitesSatisfied = curriculum.requiredPrerequisites(skillId)
                    .all { prerequisite -> (statesById[prerequisite]?.mastery ?: 0) >= 3 }
            )
        }

        val ranked = candidates.mapNotNull { candidate ->
            policy.priority(candidate)?.let { candidate to it }
        }.sortedWith(
            compareBy<Pair<AdaptiveCandidate, AdaptivePriority>> { it.second.ordinal }
                .thenBy { it.first.skillId }
        )

        val selected = ranked.firstOrNull()?.first
            ?: error("No adaptive candidate satisfies the current policy")
        val priority = policy.priority(selected)!!

        return AdaptiveDecision(
            skillId = selected.skillId,
            mode = policy.mode(selected),
            difficulty = policy.difficulty(selected).also { require(it >= 1) },
            contextType = policy.contextType(selected),
            reason = priority.name
        )
    }
}

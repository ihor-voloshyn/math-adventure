package com.mathadventure.core.adaptive

import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode

data class AdaptiveCandidate(
    val skillId: String,
    val state: SkillState,
    val requiredPrerequisitesSatisfied: Boolean
)

enum class AdaptivePriority {
    CRITICAL_REMEDIATION,
    REQUIRED_PREREQUISITE,
    RECENT_ERROR_RECOVERY,
    DUE_REVIEW,
    REINFORCE,
    ADVANCE
}

interface AdaptivePolicy {
    fun priority(candidate: AdaptiveCandidate): AdaptivePriority?
    fun mode(candidate: AdaptiveCandidate): TaskMode
    fun difficulty(candidate: AdaptiveCandidate): Int
    fun contextType(candidate: AdaptiveCandidate): String
}

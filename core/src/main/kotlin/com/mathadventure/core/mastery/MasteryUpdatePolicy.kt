package com.mathadventure.core.mastery

import com.mathadventure.core.model.MasteryUpdateInput
import com.mathadventure.core.model.SkillState

fun interface MasteryUpdatePolicy {
    fun update(current: SkillState, input: MasteryUpdateInput): SkillState
}

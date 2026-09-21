package com.mathadventure.core.mastery

import com.mathadventure.core.model.SkillState

interface MasteryStateStore {
    fun get(playerId: String, skillId: String): SkillState?
    fun put(playerId: String, state: SkillState)
}

class InMemoryMasteryStateStore : MasteryStateStore {
    private val states = mutableMapOf<Pair<String, String>, SkillState>()

    override fun get(playerId: String, skillId: String): SkillState? =
        states[playerId to skillId]

    override fun put(playerId: String, state: SkillState) {
        states[playerId to state.skillId] = state
    }
}

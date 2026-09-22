package com.mathadventure.core.gameprogression

class CoreGameProgressionFlow(
    private val rewardPolicy: GameRewardPolicy,
    private val levelPolicy: RpgLevelPolicy,
    private val unlockPolicy: GameUnlockPolicy,
    private val store: GameProgressionStore
) {
    /**
     * Builds the progression commit without persisting it.
     *
     * Coordinators that need to atomically combine progression with another
     * owned subsystem (for example Inventory) use this preparation boundary.
     */
    fun prepare(event: GameProgressionEvent): ProgressionCommit? {
        val state = store.get(event.playerId)
        if (event.eventId in state.grantedEventIds) return null

        val evaluation = rewardPolicy.evaluate(event, state)
        if (!evaluation.eligible) return null

        val levelBefore = state.rpgLevel
        val levelAfter = levelPolicy.levelFor(state.totalXp + evaluation.reward.xpDelta)
        return ProgressionCommit(
            event = event,
            reward = evaluation.reward,
            levelBefore = levelBefore,
            levelAfter = levelAfter,
            unlocks = unlockPolicy.unlocksFor(levelBefore, levelAfter),
            bestResultAfter = evaluation.bestResultAfter
        )
    }

    fun record(event: GameProgressionEvent): ProgressionCommit? {
        val commit = prepare(event) ?: return null
        store.commit(commit)
        return commit
    }
}

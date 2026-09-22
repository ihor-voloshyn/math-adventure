package com.mathadventure.core.gameprogression

class PrototypeGameRewardPolicy : GameRewardPolicy {
    override fun evaluate(
        event: GameProgressionEvent,
        state: GameProgressionState
    ): ProgressionEvaluation {
        if (event.eventType != GameEventType.COMBAT_VICTORY) {
            return ProgressionEvaluation(
                eligible = false,
                reward = RewardBundle(reason = "unsupported_event"),
                bestResultAfter = null,
                reason = "unsupported_event"
            )
        }

        val previous = state.bestResults[event.sourceId]
        if (previous != null) {
            return ProgressionEvaluation(
                eligible = false,
                reward = RewardBundle(reason = "repeat_no_reward"),
                bestResultAfter = previous,
                reason = "repeat_no_reward"
            )
        }

        return ProgressionEvaluation(
            eligible = true,
            reward = RewardBundle(
                xpDelta = 100L,
                coinsDelta = 25L,
                reason = "first_combat_victory"
            ),
            bestResultAfter = BestResult(
                sourceId = event.sourceId,
                outcome = event.outcome
            ),
            reason = "first_combat_victory"
        )
    }
}

class PrototypeGameUnlockPolicy : GameUnlockPolicy {
    override fun unlocksFor(levelBefore: Int, levelAfter: Int): Set<String> = emptySet()
}

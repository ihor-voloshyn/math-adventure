package com.mathadventure.core.gameprogression

class PrototypeGameRewardPolicy : GameRewardPolicy {
    override fun evaluate(
        event: GameProgressionEvent,
        state: GameProgressionState
    ): ProgressionEvaluation {
        return when (event.eventType) {
            GameEventType.COMBAT_VICTORY -> evaluateFirstCompletion(
                event = event,
                state = state,
                xp = 100L,
                coins = 25L,
                rewardReason = "first_combat_victory"
            )
            GameEventType.QUEST_COMPLETED -> {
                if (event.metadata["repeatability"] != "ONE_TIME") {
                    return ProgressionEvaluation(
                        eligible = false,
                        reward = RewardBundle(reason = "unsupported_quest_repeatability"),
                        bestResultAfter = null,
                        reason = "unsupported_quest_repeatability"
                    )
                }
                evaluateFirstCompletion(
                    event = event,
                    state = state,
                    xp = 75L,
                    coins = 15L,
                    rewardReason = "first_quest_completion"
                )
            }
            else -> ProgressionEvaluation(
                eligible = false,
                reward = RewardBundle(reason = "unsupported_event"),
                bestResultAfter = null,
                reason = "unsupported_event"
            )
        }
    }

    private fun evaluateFirstCompletion(
        event: GameProgressionEvent,
        state: GameProgressionState,
        xp: Long,
        coins: Long,
        rewardReason: String
    ): ProgressionEvaluation {
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
                xpDelta = xp,
                coinsDelta = coins,
                reason = rewardReason
            ),
            bestResultAfter = BestResult(
                sourceId = event.sourceId,
                outcome = event.outcome
            ),
            reason = rewardReason
        )
    }
}

class PrototypeGameUnlockPolicy : GameUnlockPolicy {
    override fun unlocksFor(levelBefore: Int, levelAfter: Int): Set<String> = emptySet()
}

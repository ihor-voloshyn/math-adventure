package com.mathadventure.core.gameprogression

class VerticalSliceRewardPolicy : GameRewardPolicy {
    override fun evaluate(event: GameProgressionEvent, state: GameProgressionState): ProgressionEvaluation {
        if (event.eventType != GameEventType.COMBAT_VICTORY) {
            return ProgressionEvaluation(false, RewardBundle(reason = "unsupported_event"), null, "unsupported_event")
        }
        if (event.sourceId == "forest-encounter-01" && state.bestResults.containsKey(event.sourceId)) {
            return ProgressionEvaluation(false, RewardBundle(reason = "already_completed"), state.bestResults[event.sourceId], "already_completed")
        }
        val best = BestResult(event.sourceId, event.outcome, score = 1)
        return ProgressionEvaluation(
            eligible = true,
            reward = RewardBundle(xpDelta = 100L, coinsDelta = 25L, reason = "first_forest_combat_victory"),
            bestResultAfter = best,
            reason = "first_forest_combat_victory"
        )
    }
}

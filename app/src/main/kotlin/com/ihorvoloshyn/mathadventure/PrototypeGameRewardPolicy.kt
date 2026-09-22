package com.ihorvoloshyn.mathadventure

import com.mathadventure.core.gameprogression.BestResult
import com.mathadventure.core.gameprogression.GameEventType
import com.mathadventure.core.gameprogression.GameProgressionEvent
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.GameRewardPolicy
import com.mathadventure.core.gameprogression.ProgressionEvaluation
import com.mathadventure.core.gameprogression.RewardBundle

/**
 * Vertical-slice tuning only. Final economy values remain configurable.
 */
class PrototypeGameRewardPolicy : GameRewardPolicy {
    override fun evaluate(event: GameProgressionEvent, state: GameProgressionState): ProgressionEvaluation {
        if (event.eventType != GameEventType.COMBAT_VICTORY) {
            return ProgressionEvaluation(false, RewardBundle(reason = "unsupported_event"), null, "unsupported_event")
        }
        if (event.sourceId in state.bestResults) {
            return ProgressionEvaluation(false, RewardBundle(reason = "repeat_no_reward"), state.bestResults[event.sourceId], "repeat_no_reward")
        }
        return ProgressionEvaluation(
            eligible = true,
            reward = RewardBundle(xpDelta = 100L, coinsDelta = 25L, reason = "first_combat_victory"),
            bestResultAfter = BestResult(event.sourceId, event.outcome),
            reason = "first_combat_victory"
        )
    }
}

class PrototypeGameUnlockPolicy : com.mathadventure.core.gameprogression.GameUnlockPolicy {
    override fun unlocksFor(levelBefore: Int, levelAfter: Int): Set<String> = emptySet()
}

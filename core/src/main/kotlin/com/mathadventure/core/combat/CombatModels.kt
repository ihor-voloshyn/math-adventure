package com.mathadventure.core.combat

/**
 * A math encounter is intentionally not a traditional RPG combat system.
 * The player has up to three attempts (hearts) to solve one math task.
 */
data class CombatState(
    val combatId: String,
    val heroHearts: Int = 3,
    val maxHeroHearts: Int = 3,
    val active: Boolean = true
) {
    init {
        require(heroHearts >= 0) { "heroHearts must be non-negative" }
        require(maxHeroHearts > 0) { "maxHeroHearts must be positive" }
        require(heroHearts <= maxHeroHearts) { "heroHearts must not exceed maxHeroHearts" }
    }

    val attemptsUsed: Int get() = maxHeroHearts - heroHearts
}

enum class CombatResolution {
    CORRECT,
    INCORRECT,
    VICTORY,
    DEFEAT
}

data class CombatOutcome(
    val state: CombatState,
    val resolution: CombatResolution,
    val gameEventType: String? = null
)

class CombatEngine {
    fun start(combatId: String): CombatState =
        CombatState(combatId = combatId, heroHearts = 3, maxHeroHearts = 3)

    /**
     * Resolves one answer to the single math task.
     * Every encounter starts with three hearts.
     * Correct answers never consume a heart and complete the quest immediately.
     * Incorrect answers consume one heart; after the third wrong answer the quest is lost.
     */
    fun resolveMathAnswer(state: CombatState, correct: Boolean): CombatOutcome {
        require(state.active) { "encounter is not active" }

        if (correct) {
            return CombatOutcome(
                state = state.copy(active = false),
                resolution = CombatResolution.VICTORY,
                gameEventType = "COMBAT_VICTORY"
            )
        }

        val hearts = (state.heroHearts - 1).coerceAtLeast(0)
        return if (hearts == 0) {
            CombatOutcome(
                state = state.copy(heroHearts = 0, active = false),
                resolution = CombatResolution.DEFEAT,
                gameEventType = "COMBAT_DEFEAT"
            )
        } else {
            CombatOutcome(
                state = state.copy(heroHearts = hearts),
                resolution = CombatResolution.INCORRECT
            )
        }
    }
}

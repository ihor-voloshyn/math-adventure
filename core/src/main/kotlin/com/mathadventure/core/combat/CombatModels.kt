package com.mathadventure.core.combat

enum class CombatAction { ATTACK, DEFEND, FLEE }

enum class CombatResolution { HIT, MISS, BLOCKED, FLED, VICTORY, DEFEAT }

data class CombatState(
    val combatId: String,
    val heroHearts: Int = 3,
    val maxHeroHearts: Int = 3,
    val enemyHp: Int = 3,
    val playerTurn: Boolean = true,
    val active: Boolean = true
) {
    init {
        require(heroHearts >= 0) { "heroHearts must be non-negative" }
        require(maxHeroHearts > 0) { "maxHeroHearts must be positive" }
        require(heroHearts <= maxHeroHearts) { "heroHearts must not exceed maxHeroHearts" }
        require(enemyHp >= 0) { "enemyHp must be non-negative" }
    }
}

data class CombatOutcome(
    val state: CombatState,
    val resolution: CombatResolution,
    val gameEventType: String? = null
)

class CombatEngine {
    fun start(combatId: String, heroHearts: Int = 3, enemyHp: Int = 3): CombatState =
        CombatState(combatId = combatId, heroHearts = heroHearts, maxHeroHearts = heroHearts, enemyHp = enemyHp)

    fun resolveMathAction(
        state: CombatState,
        action: CombatAction,
        correct: Boolean,
        attackDamage: Int = 1
    ): CombatOutcome {
        require(state.active) { "combat is not active" }
        require(state.playerTurn) { "it is not the player's turn" }
        require(attackDamage > 0) { "attackDamage must be positive" }

        if (action == CombatAction.FLEE) {
            return CombatOutcome(state.copy(active = false, playerTurn = false), CombatResolution.FLED)
        }

        if (!correct) {
            val next = state.copy(playerTurn = false)
            return enemyTurn(next, CombatResolution.MISS)
        }

        return when (action) {
            CombatAction.ATTACK -> {
                val hp = (state.enemyHp - attackDamage).coerceAtLeast(0)
                if (hp == 0) {
                    CombatOutcome(
                        state.copy(enemyHp = 0, active = false, playerTurn = false),
                        CombatResolution.VICTORY,
                        "COMBAT_VICTORY"
                    )
                } else {
                    CombatOutcome(
                        state.copy(enemyHp = hp, playerTurn = true),
                        CombatResolution.HIT
                    )
                }
            }
            CombatAction.DEFEND -> {
                // Defending consumes the player's action but blocks the enemy attack.
                // It must never remove a hero heart.
                CombatOutcome(
                    state.copy(playerTurn = true),
                    CombatResolution.BLOCKED
                )
            }
            CombatAction.FLEE -> error("handled above")
        }
    }

    private fun enemyTurn(state: CombatState, playerResolution: CombatResolution): CombatOutcome {
        val hearts = (state.heroHearts - 1).coerceAtLeast(0)
        return if (hearts == 0) {
            CombatOutcome(state.copy(heroHearts = 0, active = false, playerTurn = false), CombatResolution.DEFEAT)
        } else {
            CombatOutcome(state.copy(heroHearts = hearts, playerTurn = true), playerResolution)
        }
    }
}

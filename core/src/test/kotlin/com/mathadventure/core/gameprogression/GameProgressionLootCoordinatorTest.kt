package com.mathadventure.core.gameprogression

import com.mathadventure.core.items.DeterministicLootItemFactory
import com.mathadventure.core.items.EquipmentState
import com.mathadventure.core.items.ItemDefinition
import com.mathadventure.core.items.ItemInstance
import com.mathadventure.core.items.ItemStats
import com.mathadventure.core.items.ItemCategory
import com.mathadventure.core.items.ItemRarity
import com.mathadventure.core.items.ItemStore
import com.mathadventure.core.items.EquipmentSlot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameProgressionLootCoordinatorTest {
    private val sword = ItemDefinition(
        itemId = "sword_sparks",
        nameKey = "item.sword_sparks",
        category = ItemCategory.WEAPON,
        rarity = ItemRarity.COMMON,
        descriptionKey = "item.sword_sparks.description",
        visualId = "weapon.sword_sparks",
        stats = ItemStats(attackPower = 2),
        equipmentSlot = EquipmentSlot.WEAPON
    )

    @Test
    fun lootIsCreatedFromOneProgressionCommitWithStableInstanceIdentity() {
        val store = AtomicMemoryStore()
        val flow = CoreGameProgressionFlow(LootRewardPolicy(), DefaultRpgLevelPolicy(), EmptyUnlockPolicy(), store)
        val coordinator = GameProgressionLootCoordinator(
            flow,
            store,
            DeterministicLootItemFactory(mapOf(sword.itemId to sword))
        )

        val commit = coordinator.record(event())!!

        assertEquals(listOf("sword_sparks"), commit.reward.lootIds)
        assertEquals(1, store.inventory("player-1").size)
        assertEquals("loot-event-1-0-sword_sparks", store.inventory("player-1").single().instanceId)
        assertEquals("event-1", store.inventory("player-1").single().source)
    }

    @Test
    fun duplicateProgressionEventDoesNotCreateDuplicateLoot() {
        val store = AtomicMemoryStore()
        val flow = CoreGameProgressionFlow(LootRewardPolicy(), DefaultRpgLevelPolicy(), EmptyUnlockPolicy(), store)
        val coordinator = GameProgressionLootCoordinator(
            flow,
            store,
            DeterministicLootItemFactory(mapOf(sword.itemId to sword))
        )

        coordinator.record(event())
        assertNull(coordinator.record(event()))

        assertEquals(1, store.commits)
        assertEquals(1, store.inventory("player-1").size)
    }

    @Test
    fun noRewardEventProducesNoLoot() {
        val store = AtomicMemoryStore()
        val flow = CoreGameProgressionFlow(NoRewardPolicy(), DefaultRpgLevelPolicy(), EmptyUnlockPolicy(), store)
        val coordinator = GameProgressionLootCoordinator(
            flow,
            store,
            DeterministicLootItemFactory(mapOf(sword.itemId to sword))
        )

        assertNull(coordinator.record(event()))
        assertEquals(emptyList(), store.inventory("player-1"))
        assertEquals(0, store.commits)
    }

    private fun event() = GameProgressionEvent(
        eventId = "event-1",
        playerId = "player-1",
        eventType = GameEventType.COMBAT_VICTORY,
        sourceId = "encounter-1",
        sessionId = "session-1",
        outcome = "VICTORY",
        timestampEpochMillis = 1_000L
    )

    private class LootRewardPolicy : GameRewardPolicy {
        override fun evaluate(event: GameProgressionEvent, state: GameProgressionState) =
            ProgressionEvaluation(
                eligible = true,
                reward = RewardBundle(xpDelta = 100L, coinsDelta = 25L, lootIds = listOf("sword_sparks"), reason = "test_loot"),
                bestResultAfter = BestResult(event.sourceId, event.outcome),
                reason = "test_loot"
            )
    }

    private class NoRewardPolicy : GameRewardPolicy {
        override fun evaluate(event: GameProgressionEvent, state: GameProgressionState) =
            ProgressionEvaluation(false, RewardBundle(reason = "no_reward"), null, "no_reward")
    }

    private class EmptyUnlockPolicy : GameUnlockPolicy {
        override fun unlocksFor(levelBefore: Int, levelAfter: Int) = emptySet<String>()
    }

    private class AtomicMemoryStore : AtomicGameProgressionStore, ItemStore {
        private var state = GameProgressionState()
        private val inventories = mutableMapOf<String, List<ItemInstance>>()
        private val equipment = mutableMapOf<String, EquipmentState>()
        var commits = 0
            private set

        override fun get(playerId: String) = state

        override fun commit(commit: ProgressionCommit) {
            commitWithLoot(commit, emptyList())
        }

        override fun commitWithLoot(commit: ProgressionCommit, loot: List<ItemInstance>) {
            if (commit.event.eventId in state.grantedEventIds) return
            state = state.copy(
                totalXp = state.totalXp + commit.reward.xpDelta,
                coins = state.coins + commit.reward.coinsDelta,
                rpgLevel = commit.levelAfter,
                bestResults = commit.bestResultAfter?.let { state.bestResults + (it.sourceId to it) } ?: state.bestResults,
                grantedEventIds = state.grantedEventIds + commit.event.eventId
            )
            if (loot.isNotEmpty()) {
                val current = inventories[commit.event.playerId].orEmpty().toMutableList()
                current += loot
                inventories[commit.event.playerId] = current
            }
            commits++
        }

        override fun inventory(playerId: String) = inventories[playerId].orEmpty()
        override fun equipment(playerId: String) = equipment[playerId] ?: EquipmentState()
        override fun saveInventory(playerId: String, inventory: List<ItemInstance>) { inventories[playerId] = inventory }
        override fun saveEquipment(playerId: String, equipment: EquipmentState) { this.equipment[playerId] = equipment }
    }
}

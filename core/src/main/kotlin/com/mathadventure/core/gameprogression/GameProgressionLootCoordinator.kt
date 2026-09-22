package com.mathadventure.core.gameprogression

import com.mathadventure.core.items.DeterministicLootItemFactory
import com.mathadventure.core.items.ItemInstance

interface AtomicGameProgressionStore : GameProgressionStore {
    /**
     * Atomically commits the progression event and its derived loot.
     *
     * Implementations must treat event.eventId as the idempotency key and must
     * not persist loot when the progression event has already been granted.
     */
    fun commitWithLoot(commit: ProgressionCommit, loot: List<ItemInstance>)
}

class GameProgressionLootCoordinator(
    private val progression: CoreGameProgressionFlow,
    private val store: AtomicGameProgressionStore,
    private val lootFactory: DeterministicLootItemFactory
) {
    fun record(event: GameProgressionEvent): ProgressionCommit? {
        val commit = progression.prepare(event) ?: return null
        val loot = lootFactory.create(commit)
        store.commitWithLoot(commit, loot)
        return commit
    }
}

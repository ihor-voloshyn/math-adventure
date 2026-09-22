package com.mathadventure.core.items

import com.mathadventure.core.gameprogression.ProgressionCommit

class DeterministicLootItemFactory(
    private val definitions: Map<String, ItemDefinition>
) {
    fun create(commit: ProgressionCommit): List<ItemInstance> =
        commit.reward.lootIds.mapIndexed { index, itemId ->
            require(itemId in definitions) { "unknown loot item: $itemId" }
            ItemInstance(
                instanceId = "loot-\${commit.event.eventId}-$index-$itemId",
                playerId = commit.event.playerId,
                itemId = itemId,
                quantity = 1,
                acquiredAtEpochMillis = commit.event.timestampEpochMillis,
                source = commit.event.eventId
            )
        }
}

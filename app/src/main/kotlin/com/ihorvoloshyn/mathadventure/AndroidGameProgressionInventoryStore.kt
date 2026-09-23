package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.gameprogression.AtomicGameProgressionStore
import com.mathadventure.core.gameprogression.BestResult
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.ProgressionCommit
import com.mathadventure.core.items.EquipmentState
import com.mathadventure.core.items.EquipmentStateValidator
import com.mathadventure.core.items.ItemDefinition
import com.mathadventure.core.items.ItemInstance
import com.mathadventure.core.items.ItemStore
import org.json.JSONArray
import org.json.JSONObject

class AndroidGameProgressionInventoryStore(
    context: Context,
    private val definitions: Map<String, ItemDefinition>
) : AtomicGameProgressionStore, ItemStore {
    private val preferences = context.getSharedPreferences(
        "math_adventure_game_progression",
        Context.MODE_PRIVATE
    )

    @Synchronized
    override fun get(playerId: String): GameProgressionState = readProgression(playerId)

    @Synchronized
    override fun commit(commit: ProgressionCommit) {
        commitWithLoot(commit, emptyList())
    }

    @Synchronized
    override fun commitWithLoot(commit: ProgressionCommit, loot: List<ItemInstance>) {
        val playerId = commit.event.playerId
        val currentProgression = readProgression(playerId)
        if (commit.event.eventId in currentProgression.grantedEventIds) return

        validateInventory(playerId, loot)

        val bestResults = currentProgression.bestResults.toMutableMap()
        commit.bestResultAfter?.let { bestResults[it.sourceId] = it }

        val nextProgression = currentProgression.copy(
            totalXp = currentProgression.totalXp + commit.reward.xpDelta,
            coins = currentProgression.coins + commit.reward.coinsDelta,
            rpgLevel = commit.levelAfter,
            bestResults = bestResults,
            grantedEventIds = currentProgression.grantedEventIds + commit.event.eventId,
            unlockedIds = currentProgression.unlockedIds + commit.unlocks
        )

        val nextInventory = readInventory(playerId).toMutableList()
        loot.forEach { item ->
            val definition = definitions[item.itemId]!!
            if (definition.category == com.mathadventure.core.items.ItemCategory.CONSUMABLE &&
                definition.equipmentSlot == null
            ) {
                val index = nextInventory.indexOfFirst { it.itemId == item.itemId }
                if (index >= 0) {
                    val existing = nextInventory[index]
                    nextInventory[index] = existing.copy(quantity = existing.quantity + item.quantity)
                } else {
                    nextInventory += item
                }
            } else {
                nextInventory += item
            }
        }

        persistAtomic(
            progressionKey(playerId),
            encodeProgression(nextProgression),
            inventoryKey(playerId),
            encodeInventory(nextInventory)
        )
    }

    @Synchronized
    override fun inventory(playerId: String): List<ItemInstance> = readInventory(playerId)

    @Synchronized
    override fun equipment(playerId: String): EquipmentState = readEquipment(playerId)

    @Synchronized
    override fun saveInventory(playerId: String, inventory: List<ItemInstance>) {
        validateInventory(playerId, inventory)
        persistAtomic(inventoryKey(playerId), encodeInventory(inventory))
    }

    @Synchronized
    override fun saveEquipment(playerId: String, equipment: EquipmentState) {
        validateEquipment(playerId, equipment)
        persistAtomic(equipmentKey(playerId), encodeEquipment(equipment))
    }

    private fun validateInventory(playerId: String, inventory: List<ItemInstance>) {
        val instanceIds = mutableSetOf<String>()
        inventory.forEach { item ->
            require(item.playerId == playerId) { "inventory player mismatch" }
            require(item.itemId in definitions) { "unknown inventory item: ${item.itemId}" }
            require(instanceIds.add(item.instanceId)) { "duplicate inventory instance: ${item.instanceId}" }
        }
    }

    private fun validateEquipment(playerId: String, equipment: EquipmentState) {
        val inventory = readInventory(playerId)
        EquipmentStateValidator(definitions).validate(inventory, equipment)
    }

    private fun persistAtomic(vararg entries: String) {
        require(entries.size % 2 == 0)
        val editor = preferences.edit()
        entries.asList().chunked(2).forEach { (key, value) -> editor.putString(key, value) }
        check(editor.commit()) { "failed to persist game state" }
    }

    private fun progressionKey(playerId: String) = "state:" + playerId
    private fun inventoryKey(playerId: String) = "inventory:" + playerId
    private fun equipmentKey(playerId: String) = "equipment:" + playerId

    private fun readProgression(playerId: String): GameProgressionState {
        val raw = preferences.getString(progressionKey(playerId), null) ?: return GameProgressionState()
        val json = JSONObject(raw)
        val bestResults = mutableMapOf<String, BestResult>()
        val results = json.optJSONArray("bestResults") ?: JSONArray()
        for (i in 0 until results.length()) {
            val item = results.getJSONObject(i)
            val result = BestResult(
                sourceId = item.getString("sourceId"),
                outcome = item.getString("outcome"),
                score = if (item.isNull("score")) null else item.getInt("score"),
                completionState = item.optString("completionState", "COMPLETED")
            )
            bestResults[result.sourceId] = result
        }
        val grantedEventIds = buildSet {
            val ids = json.optJSONArray("grantedEventIds") ?: JSONArray()
            for (i in 0 until ids.length()) add(ids.getString(i))
        }
        val unlockedIds = buildSet {
            val ids = json.optJSONArray("unlockedIds") ?: JSONArray()
            for (i in 0 until ids.length()) add(ids.getString(i))
        }
        return GameProgressionState(
            totalXp = json.optLong("totalXp", 0L),
            coins = json.optLong("coins", 0L),
            rpgLevel = json.optInt("rpgLevel", 1),
            bestResults = bestResults,
            grantedEventIds = grantedEventIds,
            unlockedIds = unlockedIds
        )
    }

    private fun readInventory(playerId: String): List<ItemInstance> {
        val raw = preferences.getString(inventoryKey(playerId), null) ?: return emptyList()
        val array = JSONArray(raw)
        return buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    ItemInstance(
                        instanceId = item.getString("instanceId"),
                        playerId = item.getString("playerId"),
                        itemId = item.getString("itemId"),
                        quantity = item.getInt("quantity"),
                        acquiredAtEpochMillis = item.getLong("acquiredAtEpochMillis"),
                        source = item.getString("source")
                    )
                )
            }
        }
    }

    private fun readEquipment(playerId: String): EquipmentState {
        val raw = preferences.getString(equipmentKey(playerId), null) ?: return EquipmentState()
        val json = JSONObject(raw)
        return EquipmentState(
            weaponInstanceId = json.optStringOrNull("weaponInstanceId"),
            armorInstanceId = json.optStringOrNull("armorInstanceId"),
            helmetInstanceId = json.optStringOrNull("helmetInstanceId"),
            accessoryInstanceId = json.optStringOrNull("accessoryInstanceId"),
            petAccessoryInstanceId = json.optStringOrNull("petAccessoryInstanceId")
        )
    }

    private fun encodeProgression(state: GameProgressionState): String =
        JSONObject()
            .put("totalXp", state.totalXp)
            .put("coins", state.coins)
            .put("rpgLevel", state.rpgLevel)
            .put("bestResults", JSONArray().apply {
                state.bestResults.values.forEach { result ->
                    put(
                        JSONObject()
                            .put("sourceId", result.sourceId)
                            .put("outcome", result.outcome)
                            .put("score", result.score)
                            .put("completionState", result.completionState)
                    )
                }
            })
            .put("grantedEventIds", JSONArray().apply { state.grantedEventIds.forEach(::put) })
            .put("unlockedIds", JSONArray().apply { state.unlockedIds.forEach(::put) })
            .toString()

    private fun encodeInventory(inventory: List<ItemInstance>): String =
        JSONArray().apply {
            inventory.forEach { item ->
                put(
                    JSONObject()
                        .put("instanceId", item.instanceId)
                        .put("playerId", item.playerId)
                        .put("itemId", item.itemId)
                        .put("quantity", item.quantity)
                        .put("acquiredAtEpochMillis", item.acquiredAtEpochMillis)
                        .put("source", item.source)
                )
            }
        }.toString()

    private fun encodeEquipment(equipment: EquipmentState): String =
        JSONObject()
            .putNullable("weaponInstanceId", equipment.weaponInstanceId)
            .putNullable("armorInstanceId", equipment.armorInstanceId)
            .putNullable("helmetInstanceId", equipment.helmetInstanceId)
            .putNullable("accessoryInstanceId", equipment.accessoryInstanceId)
            .putNullable("petAccessoryInstanceId", equipment.petAccessoryInstanceId)
            .toString()

    private fun JSONObject.putNullable(key: String, value: String?): JSONObject {
        put(key, value)
        return this
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key, null)
}

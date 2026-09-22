package com.mathadventure.core.items

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ItemEngineTest {
    private val sword = ItemDefinition(
        itemId = "sword_sparks",
        nameKey = "item.sword_sparks",
        category = ItemCategory.WEAPON,
        rarity = ItemRarity.COMMON,
        descriptionKey = "item.sword_sparks.description",
        visualId = "weapon.sword_sparks",
        stats = ItemStats(attackPower = 2),
        equipmentSlot = EquipmentSlot.WEAPON,
        requiredRpgLevel = 2
    )
    private val potion = ItemDefinition(
        itemId = "potion_small",
        nameKey = "item.potion_small",
        category = ItemCategory.CONSUMABLE,
        rarity = ItemRarity.COMMON,
        descriptionKey = "item.potion_small.description",
        visualId = "consumable.potion_small"
    )

    @Test
    fun consumablesStackButEquipmentInstancesRemainSeparate() {
        val store = MemoryStore()
        val engine = ItemEngine(mapOf(sword.itemId to sword, potion.itemId to potion), store)

        engine.addItem(ItemInstance("p1", "player", potion.itemId, 1, 1L, "quest"))
        engine.addItem(ItemInstance("p2", "player", potion.itemId, 2, 2L, "combat"))
        engine.addItem(ItemInstance("s1", "player", sword.itemId, 1, 3L, "combat"))
        engine.addItem(ItemInstance("s2", "player", sword.itemId, 1, 4L, "combat"))

        assertEquals(3, engine.getInventory("player").first { it.itemId == potion.itemId }.quantity)
        assertEquals(2, engine.getInventory("player").count { it.itemId == sword.itemId })
    }

    @Test
    fun equipRequiresOwnershipAndRpgLevel() {
        val store = MemoryStore()
        val engine = ItemEngine(mapOf(sword.itemId to sword), store)
        engine.addItem(ItemInstance("s1", "player", sword.itemId, 1, 1L, "quest"))

        assertFalse(engine.canEquip("player", "s1", 1))
        assertTrue(engine.canEquip("player", "s1", 2))
        assertFailsWith<IllegalArgumentException> { engine.equip("player", "s1", 1) }
        engine.equip("player", "s1", 2)
        assertEquals("s1", engine.getEquipment("player").weaponInstanceId)
    }

    @Test
    fun replacementReturnsPreviousItemToInventory() {
        val second = sword.copy(itemId = "sword_ember", visualId = "weapon.sword_ember")
        val store = MemoryStore()
        val engine = ItemEngine(mapOf(sword.itemId to sword, second.itemId to second), store)
        engine.addItem(ItemInstance("s1", "player", sword.itemId, 1, 1L, "quest"))
        engine.addItem(ItemInstance("s2", "player", second.itemId, 1, 2L, "quest"))

        engine.equip("player", "s1", 2)
        engine.equip("player", "s2", 2)

        assertEquals("s2", engine.getEquipment("player").weaponInstanceId)
        assertTrue(engine.getInventory("player").any { it.instanceId == "s1" })
    }

    @Test
    fun unequipKeepsItemOwned() {
        val store = MemoryStore()
        val engine = ItemEngine(mapOf(sword.itemId to sword), store)
        engine.addItem(ItemInstance("s1", "player", sword.itemId, 1, 1L, "quest"))
        engine.equip("player", "s1", 2)
        engine.unequip("player", EquipmentSlot.WEAPON)

        assertEquals(null, engine.getEquipment("player").weaponInstanceId)
        assertTrue(engine.getInventory("player").any { it.instanceId == "s1" })
    }

    private class MemoryStore : ItemStore {
        private val inventories = mutableMapOf<String, List<ItemInstance>>()
        private val equipment = mutableMapOf<String, EquipmentState>()

        override fun inventory(playerId: String) = inventories[playerId] ?: emptyList()
        override fun equipment(playerId: String) = equipment[playerId] ?: EquipmentState()
        override fun saveInventory(playerId: String, inventory: List<ItemInstance>) {
            inventories[playerId] = inventory
        }
        override fun saveEquipment(playerId: String, equipment: EquipmentState) {
            this.equipment[playerId] = equipment
        }
    }
}

package com.mathadventure.core.combat

import com.mathadventure.core.items.EquipmentSlot
import com.mathadventure.core.items.EquipmentState
import com.mathadventure.core.items.ItemCategory
import com.mathadventure.core.items.ItemDefinition
import com.mathadventure.core.items.ItemInstance
import com.mathadventure.core.items.ItemRarity
import com.mathadventure.core.items.ItemStats
import kotlin.test.Test
import kotlin.test.assertFailsWith

class EquipmentCombatStatsResolverInvariantTest {
    private val sword = ItemDefinition(
        itemId = "sword",
        nameKey = "sword",
        category = ItemCategory.WEAPON,
        rarity = ItemRarity.COMMON,
        descriptionKey = "sword",
        visualId = "sword",
        equipmentSlot = EquipmentSlot.WEAPON
    )

    private val armor = ItemDefinition(
        itemId = "armor",
        nameKey = "armor",
        category = ItemCategory.ARMOR,
        rarity = ItemRarity.COMMON,
        descriptionKey = "armor",
        visualId = "armor",
        equipmentSlot = EquipmentSlot.ARMOR
    )

    private val resolver = EquipmentCombatStatsResolver(
        mapOf(sword.itemId to sword, armor.itemId to armor)
    )

    @Test
    fun equippedItemMustBeOwned() {
        assertFailsWith<IllegalStateException> {
            resolver.resolve(emptyList(), EquipmentState(weaponInstanceId = "missing"))
        }
    }

    @Test
    fun equippedItemMustMatchSlot() {
        val armorInstance = ItemInstance("a1", "player", "armor", 1, 1L, "quest")

        assertFailsWith<IllegalArgumentException> {
            resolver.resolve(listOf(armorInstance), EquipmentState(weaponInstanceId = "a1"))
        }
    }

    @Test
    fun sameInstanceCannotOccupyTwoSlots() {
        val swordInstance = ItemInstance("s1", "player", "sword", 1, 1L, "combat")

        assertFailsWith<IllegalArgumentException> {
            resolver.resolve(
                listOf(swordInstance),
                EquipmentState(weaponInstanceId = "s1", armorInstanceId = "s1")
            )
        }
    }

    @Test
    fun equippedItemMustBeASeparateInstance() {
        val swordInstance = ItemInstance("s1", "player", "sword", 2, 1L, "combat")

        assertFailsWith<IllegalArgumentException> {
            resolver.resolve(listOf(swordInstance), EquipmentState(weaponInstanceId = "s1"))
        }
    }
}

package com.mathadventure.core.combat

import com.mathadventure.core.items.EquipmentSlot
import com.mathadventure.core.items.EquipmentState
import com.mathadventure.core.items.ItemCategory
import com.mathadventure.core.items.ItemDefinition
import com.mathadventure.core.items.ItemInstance
import com.mathadventure.core.items.ItemRarity
import com.mathadventure.core.items.ItemStats
import kotlin.test.Test
import kotlin.test.assertEquals

class EquipmentCombatStatsResolverTest {
    private val sword = ItemDefinition(
        itemId = "sword",
        nameKey = "sword",
        category = ItemCategory.WEAPON,
        rarity = ItemRarity.COMMON,
        descriptionKey = "sword",
        visualId = "sword",
        stats = ItemStats(attackPower = 1),
        equipmentSlot = EquipmentSlot.WEAPON
    )

    private val armor = ItemDefinition(
        itemId = "armor",
        nameKey = "armor",
        category = ItemCategory.ARMOR,
        rarity = ItemRarity.COMMON,
        descriptionKey = "armor",
        visualId = "armor",
        stats = ItemStats(defense = 2, hearts = 1),
        equipmentSlot = EquipmentSlot.ARMOR
    )

    private val resolver = EquipmentCombatStatsResolver(
        mapOf(sword.itemId to sword, armor.itemId to armor)
    )

    @Test
    fun noEquipmentUsesBaseStats() {
        assertEquals(CombatStats(), resolver.resolve(emptyList(), EquipmentState()))
    }

    @Test
    fun equippedWeaponAddsAttack() {
        val swordInstance = ItemInstance("s1", "player", "sword", 1, 1L, "combat")
        val state = EquipmentState(weaponInstanceId = "s1")

        assertEquals(2, resolver.resolve(listOf(swordInstance), state).attackPower)
    }

    @Test
    fun equippedArmorAddsDefenseAndHearts() {
        val armorInstance = ItemInstance("a1", "player", "armor", 1, 1L, "quest")
        val state = EquipmentState(armorInstanceId = "a1")
        val stats = resolver.resolve(listOf(armorInstance), state)

        assertEquals(1, stats.attackPower)
        assertEquals(2, stats.defense)
        assertEquals(1, stats.hearts)
    }

    @Test
    fun unequippedItemDoesNotAffectStats() {
        val swordInstance = ItemInstance("s1", "player", "sword", 1, 1L, "combat")

        assertEquals(
            1,
            resolver.resolve(listOf(swordInstance), EquipmentState()).attackPower
        )
    }
}

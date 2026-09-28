package com.ihorvoloshyn.mathadventure

import com.mathadventure.core.items.EquipmentSlot
import com.mathadventure.core.items.ItemCategory
import com.mathadventure.core.items.ItemDefinition
import com.mathadventure.core.items.ItemRarity
import com.mathadventure.core.items.ItemStats

object PrototypeItemCatalog {
    val definitions: Map<String, ItemDefinition> = listOf(
        ItemDefinition(
            itemId = "sword_sparks",
            nameKey = "item.sword_sparks.name",
            category = ItemCategory.WEAPON,
            rarity = ItemRarity.COMMON,
            descriptionKey = "item.sword_sparks.description",
            visualId = "weapon_sword_sparks",
            stats = ItemStats(attackPower = 1),
            equipmentSlot = EquipmentSlot.WEAPON,
            requiredRpgLevel = 1
        ),
        ItemDefinition(
            itemId = "guardian_vest",
            nameKey = "item.guardian_vest.name",
            category = ItemCategory.ARMOR,
            rarity = ItemRarity.COMMON,
            descriptionKey = "item.guardian_vest.description",
            visualId = "armor_guardian_vest",
            stats = ItemStats(hearts = 1),
            equipmentSlot = EquipmentSlot.ARMOR,
            requiredRpgLevel = 1
        )
    ).associateBy { it.itemId }
}

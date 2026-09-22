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
        )
    ).associateBy { it.itemId }
}

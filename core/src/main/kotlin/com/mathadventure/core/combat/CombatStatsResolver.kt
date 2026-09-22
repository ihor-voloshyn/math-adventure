package com.mathadventure.core.combat

import com.mathadventure.core.items.EquipmentState
import com.mathadventure.core.items.ItemDefinition
import com.mathadventure.core.items.ItemInstance

data class CombatStats(
    val attackPower: Int = 1,
    val defense: Int = 0,
    val hearts: Int = 0,
    val abilityModifier: Int = 0
)

class EquipmentCombatStatsResolver(
    private val definitions: Map<String, ItemDefinition>
) {
    fun resolve(
        inventory: List<ItemInstance>,
        equipment: EquipmentState
    ): CombatStats {
        val equippedIds = listOfNotNull(
            equipment.weaponInstanceId,
            equipment.armorInstanceId,
            equipment.helmetInstanceId,
            equipment.accessoryInstanceId,
            equipment.petAccessoryInstanceId
        ).toSet()

        val equippedDefinitions = inventory
            .filter { it.instanceId in equippedIds }
            .map { definitions[it.itemId] ?: error("unknown item: ${it.itemId}") }

        return CombatStats(
            attackPower = 1 + equippedDefinitions.sumOf { it.stats.attackPower },
            defense = equippedDefinitions.sumOf { it.stats.defense },
            hearts = equippedDefinitions.sumOf { it.stats.hearts },
            abilityModifier = equippedDefinitions.sumOf { it.stats.abilityModifier }
        )
    }
}

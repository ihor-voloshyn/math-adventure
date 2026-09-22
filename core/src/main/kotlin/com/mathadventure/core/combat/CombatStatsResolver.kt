package com.mathadventure.core.combat

import com.mathadventure.core.items.EquipmentSlot
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
        val equippedBySlot = listOf(
            EquipmentSlot.WEAPON to equipment.weaponInstanceId,
            EquipmentSlot.ARMOR to equipment.armorInstanceId,
            EquipmentSlot.HELMET to equipment.helmetInstanceId,
            EquipmentSlot.ACCESSORY to equipment.accessoryInstanceId,
            EquipmentSlot.PET_ACCESSORY to equipment.petAccessoryInstanceId
        ).mapNotNull { (slot, instanceId) ->
            instanceId?.let { slot to it }
        }

        val equippedIds = equippedBySlot.map { it.second }
        require(equippedIds.size == equippedIds.toSet().size) {
            "an item instance cannot occupy multiple equipment slots"
        }

        val inventoryById = inventory.associateBy { it.instanceId }
        require(inventoryById.size == inventory.size) {
            "inventory contains duplicate instance IDs"
        }

        val equippedDefinitions = equippedBySlot.map { (slot, instanceId) ->
            val instance = inventoryById[instanceId]
                ?: error("equipped item is not owned: ${instanceId}")
            require(instance.quantity == 1) {
                "equipped item must have quantity 1: ${instanceId}"
            }

            val definition = definitions[instance.itemId]
                ?: error("unknown item: ${instance.itemId}")
            require(definition.equipmentSlot == slot) {
                "item ${instance.itemId} is not compatible with slot $slot"
            }

            definition
        }

        return CombatStats(
            attackPower = 1 + equippedDefinitions.sumOf { it.stats.attackPower },
            defense = equippedDefinitions.sumOf { it.stats.defense },
            hearts = equippedDefinitions.sumOf { it.stats.hearts },
            abilityModifier = equippedDefinitions.sumOf { it.stats.abilityModifier }
        )
    }
}

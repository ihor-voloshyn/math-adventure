package com.mathadventure.core.items

class EquipmentStateValidator(
    private val definitions: Map<String, ItemDefinition>
) {
    fun validate(inventory: List<ItemInstance>, equipment: EquipmentState) {
        val inventoryById = inventory.associateBy { it.instanceId }
        require(inventoryById.size == inventory.size) { "inventory contains duplicate instance IDs" }
        val equippedBySlot = listOf(
            EquipmentSlot.WEAPON to equipment.weaponInstanceId,
            EquipmentSlot.ARMOR to equipment.armorInstanceId,
            EquipmentSlot.HELMET to equipment.helmetInstanceId,
            EquipmentSlot.ACCESSORY to equipment.accessoryInstanceId,
            EquipmentSlot.PET_ACCESSORY to equipment.petAccessoryInstanceId
        ).mapNotNull { (slot, instanceId) -> instanceId?.let { slot to it } }
        val equippedIds = equippedBySlot.map { it.second }
        require(equippedIds.size == equippedIds.toSet().size) { "an item instance cannot occupy multiple equipment slots" }
        equippedBySlot.forEach { (slot, instanceId) ->
            val instance = inventoryById[instanceId] ?: error("equipped item is not owned: " + instanceId)
            require(instance.quantity == 1) { "equipped item must have quantity 1: " + instanceId }
            val definition = definitions[instance.itemId] ?: error("unknown equipped item: " + instance.itemId)
            require(definition.equipmentSlot == slot) { "item " + instance.itemId + " is not compatible with slot " + slot }
        }
    }
}

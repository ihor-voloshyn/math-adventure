package com.mathadventure.core.items

class ItemEngine(
    private val definitions: Map<String, ItemDefinition>,
    private val store: ItemStore
) {
    fun getItemDefinition(itemId: String): ItemDefinition =
        definitions[itemId] ?: error("unknown item: $itemId")

    fun getInventory(playerId: String): List<ItemInstance> = store.inventory(playerId)

    fun getEquipment(playerId: String): EquipmentState = store.equipment(playerId)

    fun addItem(item: ItemInstance) {
        require(item.itemId in definitions) { "unknown item: ${item.itemId}" }
        require(item.quantity > 0) { "quantity must be positive" }

        val current = store.inventory(item.playerId).toMutableList()
        val definition = getItemDefinition(item.itemId)
        if (definition.equipmentSlot == null && definition.category == ItemCategory.CONSUMABLE) {
            val existingIndex = current.indexOfFirst { it.itemId == item.itemId }
            if (existingIndex >= 0) {
                val existing = current[existingIndex]
                current[existingIndex] = existing.copy(quantity = existing.quantity + item.quantity)
            } else {
                current += item
            }
        } else {
            current += item
        }
        store.saveInventory(item.playerId, current)
    }

    fun canEquip(playerId: String, itemInstanceId: String, rpgLevel: Int): Boolean =
        runCatching {
            require(rpgLevel in 1..30)
            val instance = ownedInstance(playerId, itemInstanceId)
            val definition = getItemDefinition(instance.itemId)
            val slot = definition.equipmentSlot ?: return false
            rpgLevel >= definition.requiredRpgLevel && instance.quantity == 1 && getEquipment(playerId).instanceFor(slot) != itemInstanceId
        }.getOrDefault(false)

    fun equip(playerId: String, itemInstanceId: String, rpgLevel: Int) {
        require(canEquip(playerId, itemInstanceId, rpgLevel)) { "item cannot be equipped: $itemInstanceId" }

        val instance = ownedInstance(playerId, itemInstanceId)
        val slot = getItemDefinition(instance.itemId).equipmentSlot!!
        val current = getEquipment(playerId)
        val previous = current.instanceFor(slot)
        if (previous != null && previous != itemInstanceId) {
            require(store.inventory(playerId).any { it.instanceId == previous }) {
                "equipped replacement item is not owned: $previous"
            }
        }
        store.saveEquipment(playerId, current.with(slot, itemInstanceId))
    }

    fun unequip(playerId: String, slot: EquipmentSlot) {
        val current = getEquipment(playerId)
        val instanceId = current.instanceFor(slot) ?: return
        require(store.inventory(playerId).any { it.instanceId == instanceId }) {
            "equipped item is not owned: $instanceId"
        }
        store.saveEquipment(playerId, current.with(slot, null))
    }

    private fun ownedInstance(playerId: String, instanceId: String): ItemInstance =
        store.inventory(playerId).firstOrNull { it.instanceId == instanceId }
            ?: error("item is not owned: $instanceId")
}

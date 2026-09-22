package com.mathadventure.core.items

enum class ItemCategory {
    WEAPON, ARMOR, HELMET, ACCESSORY, CONSUMABLE, QUEST_ITEM, DECORATION
}

enum class ItemRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}

enum class EquipmentSlot {
    WEAPON, ARMOR, HELMET, ACCESSORY, PET_ACCESSORY
}

data class ItemStats(
    val attackPower: Int = 0,
    val defense: Int = 0,
    val hearts: Int = 0,
    val abilityModifier: Int = 0
) {
    init {
        require(attackPower >= 0) { "attackPower must be non-negative" }
        require(defense >= 0) { "defense must be non-negative" }
        require(hearts >= 0) { "hearts must be non-negative" }
        require(abilityModifier >= 0) { "abilityModifier must be non-negative" }
    }
}

data class ItemDefinition(
    val itemId: String,
    val nameKey: String,
    val category: ItemCategory,
    val rarity: ItemRarity,
    val descriptionKey: String,
    val visualId: String,
    val stats: ItemStats = ItemStats(),
    val equipmentSlot: EquipmentSlot? = null,
    val requiredRpgLevel: Int = 1
) {
    init {
        require(itemId.isNotBlank()) { "itemId must not be blank" }
        require(nameKey.isNotBlank()) { "nameKey must not be blank" }
        require(visualId.isNotBlank()) { "visualId must not be blank" }
        require(requiredRpgLevel in 1..30) { "requiredRpgLevel must be 1..30" }
        if (equipmentSlot != null) {
            require(category != ItemCategory.CONSUMABLE) { "consumables cannot be equipment" }
        }
    }
}

data class ItemInstance(
    val instanceId: String,
    val playerId: String,
    val itemId: String,
    val quantity: Int,
    val acquiredAtEpochMillis: Long,
    val source: String
) {
    init {
        require(instanceId.isNotBlank()) { "instanceId must not be blank" }
        require(playerId.isNotBlank()) { "playerId must not be blank" }
        require(itemId.isNotBlank()) { "itemId must not be blank" }
        require(quantity > 0) { "quantity must be positive" }
    }
}

data class EquipmentState(
    val weaponInstanceId: String? = null,
    val armorInstanceId: String? = null,
    val helmetInstanceId: String? = null,
    val accessoryInstanceId: String? = null,
    val petAccessoryInstanceId: String? = null
) {
    fun instanceFor(slot: EquipmentSlot): String? = when (slot) {
        EquipmentSlot.WEAPON -> weaponInstanceId
        EquipmentSlot.ARMOR -> armorInstanceId
        EquipmentSlot.HELMET -> helmetInstanceId
        EquipmentSlot.ACCESSORY -> accessoryInstanceId
        EquipmentSlot.PET_ACCESSORY -> petAccessoryInstanceId
    }

    fun with(slot: EquipmentSlot, instanceId: String?): EquipmentState = when (slot) {
        EquipmentSlot.WEAPON -> copy(weaponInstanceId = instanceId)
        EquipmentSlot.ARMOR -> copy(armorInstanceId = instanceId)
        EquipmentSlot.HELMET -> copy(helmetInstanceId = instanceId)
        EquipmentSlot.ACCESSORY -> copy(accessoryInstanceId = instanceId)
        EquipmentSlot.PET_ACCESSORY -> copy(petAccessoryInstanceId = instanceId)
    }
}

interface ItemStore {
    fun inventory(playerId: String): List<ItemInstance>
    fun equipment(playerId: String): EquipmentState
    fun saveInventory(playerId: String, inventory: List<ItemInstance>)
    fun saveEquipment(playerId: String, equipment: EquipmentState)
}

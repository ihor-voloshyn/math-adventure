package com.mathadventure.core.items

import kotlin.test.Test
import kotlin.test.assertFailsWith

class EquipmentStateValidatorTest {
    private val sword = ItemDefinition("sword", "sword", ItemCategory.WEAPON, ItemRarity.COMMON, "sword", "sword", equipmentSlot = EquipmentSlot.WEAPON)
    private val armor = ItemDefinition("armor", "armor", ItemCategory.ARMOR, ItemRarity.COMMON, "armor", "armor", equipmentSlot = EquipmentSlot.ARMOR)
    private val validator = EquipmentStateValidator(mapOf(sword.itemId to sword, armor.itemId to armor))
    private fun item(id: String, itemId: String, quantity: Int = 1) = ItemInstance(id, "player", itemId, quantity, 1L, "test")
    @Test fun validEquipmentIsAccepted() = validator.validate(listOf(item("s1", "sword"), item("a1", "armor")), EquipmentState("s1", "a1"))
    @Test fun missingOwnedItemIsRejected() { assertFailsWith<IllegalStateException> { validator.validate(emptyList(), EquipmentState(weaponInstanceId = "missing")) } }
    @Test fun wrongSlotIsRejected() { assertFailsWith<IllegalArgumentException> { validator.validate(listOf(item("a1", "armor")), EquipmentState(weaponInstanceId = "a1")) } }
    @Test fun stackedEquipmentIsRejected() { assertFailsWith<IllegalArgumentException> { validator.validate(listOf(item("s1", "sword", 2)), EquipmentState(weaponInstanceId = "s1")) } }
    @Test fun duplicateSlotUsageIsRejected() { assertFailsWith<IllegalArgumentException> { validator.validate(listOf(item("s1", "sword")), EquipmentState("s1", armorInstanceId = "s1")) } }
}

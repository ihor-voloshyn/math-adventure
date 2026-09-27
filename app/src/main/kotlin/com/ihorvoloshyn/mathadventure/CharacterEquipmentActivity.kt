package com.ihorvoloshyn.mathadventure

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.drawable.GradientDrawable
import com.mathadventure.core.combat.EquipmentCombatStatsResolver

class CharacterEquipmentActivity : Activity() {
    private val playerId = "prototype-player"
    private lateinit var renderer: AdventureRenderer
    private lateinit var gameProgressionStore: AndroidGameProgressionInventoryStore
    private lateinit var itemEngine: com.mathadventure.core.items.ItemEngine
    private lateinit var characterPreferences: android.content.SharedPreferences
    private lateinit var summary: TextView
    private lateinit var weaponButton: Button
    private lateinit var armorButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        characterPreferences = getSharedPreferences("math_adventure_character", MODE_PRIVATE)
        gameProgressionStore = AndroidGameProgressionInventoryStore(this, PrototypeItemCatalog.definitions)
        itemEngine = com.mathadventure.core.items.ItemEngine(PrototypeItemCatalog.definitions, gameProgressionStore)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            background = panelBackground(0xFF101722.toInt(), 0f)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = textView(27f).apply {
            text = "⚔ Персонаж и снаряжение"
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val close = gameButton().apply {
            text = "✕"
            setTextSize(20f)
            setOnClickListener { finish() }
        }
        header.addView(title, LinearLayout.LayoutParams(0, 58, 1f))
        header.addView(close, LinearLayout.LayoutParams(58, 58))
        root.addView(header)

        renderer = AdventureRenderer(this)
        root.addView(renderer, LinearLayout.LayoutParams(-1, 0, 1.25f))

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 16, 18, 16)
            background = panelBackground(0xB82D3A4A.toInt(), 24f)
        }
        summary = textView(17f)
        card.addView(summary)
        root.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 10 })

        weaponButton = gameButton().apply {
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            setOnClickListener { toggleWeapon() }
        }
        root.addView(weaponButton, LinearLayout.LayoutParams(-1, 64).apply { bottomMargin = 8 })

        armorButton = gameButton().apply {
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            setOnClickListener { toggleArmor() }
        }
        root.addView(armorButton, LinearLayout.LayoutParams(-1, 64))

        setContentView(root)
        render()
    }

    private fun toggleWeapon() {
        val equipment = itemEngine.getEquipment(playerId)
        if (equipment.weaponInstanceId != null) {
            itemEngine.unequip(playerId, com.mathadventure.core.items.EquipmentSlot.WEAPON)
        } else {
            val sword = itemEngine.getInventory(playerId).firstOrNull { it.itemId == "sword_sparks" } ?: return
            itemEngine.equip(playerId, sword.instanceId, gameProgressionStore.get(playerId).rpgLevel)
        }
        render()
    }

    private fun toggleArmor() {
        val equipment = itemEngine.getEquipment(playerId)
        if (equipment.armorInstanceId != null) {
            itemEngine.unequip(playerId, com.mathadventure.core.items.EquipmentSlot.ARMOR)
        } else {
            val vest = itemEngine.getInventory(playerId).firstOrNull { it.itemId == "guardian_vest" } ?: return
            itemEngine.equip(playerId, vest.instanceId, gameProgressionStore.get(playerId).rpgLevel)
        }
        render()
    }

    private fun render() {
        renderer.setStage(0)
        renderer.setHeroKind("CAT")
        renderer.setHeroClass("KNIGHT")

        val inventory = itemEngine.getInventory(playerId)
        val equipment = itemEngine.getEquipment(playerId)
        val stats = EquipmentCombatStatsResolver(PrototypeItemCatalog.definitions).resolve(inventory, equipment)
        renderer.setEquippedWeapon(equipment.weaponInstanceId?.let { id ->
            inventory.firstOrNull { it.instanceId == id }?.let { itemEngine.getItemDefinition(it.itemId).visualId }
        })
        renderer.setEquippedArmor(equipment.armorInstanceId?.let { id ->
            inventory.firstOrNull { it.instanceId == id }?.let { itemEngine.getItemDefinition(it.itemId).visualId }
        })

        val level = gameProgressionStore.get(playerId).rpgLevel
        summary.text = "⚔ Рыцарь    ❤️ " + stats.hearts + "    ⚔ " + stats.attackPower + "    ⭐ Уровень " + level

        weaponButton.text = if (equipment.weaponInstanceId != null) {
            "⚔ Меч Искры — экипирован\\nНажми, чтобы снять"
        } else {
            "⚔ Меч Искры — в инвентаре\\nНажми, чтобы экипировать"
        }
        armorButton.text = if (equipment.armorInstanceId != null) {
            "🛡 Кирасa стража — экипирована\\nНажми, чтобы снять"
        } else {
            "🛡 Кирасa стража — в инвентаре\\nНажми, чтобы экипировать"
        }
    }

    private fun textView(size: Float) = TextView(this).apply {
        textSize = size
        setTextColor(Color.WHITE)
        setShadowLayer(5f, 2f, 2f, Color.BLACK)
    }

    private fun gameButton() = Button(this).apply {
        setTextColor(Color.WHITE)
        textSize = 15f
        isAllCaps = false
        minHeight = 0
        minimumHeight = 0
        setPadding(10, 0, 10, 0)
        background = panelBackground(0xE52D3A4A.toInt(), 22f)
        stateListAnimator = null
    }

    private fun panelBackground(color: Int, radius: Float) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }
}

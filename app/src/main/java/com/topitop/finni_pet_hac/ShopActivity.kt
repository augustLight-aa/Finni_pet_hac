package com.topitop.finni_pet_hac

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShopActivity : AppCompatActivity() {

    private var balance: Int = 0
    private var planMandatory: Int = 0
    private var planOptional: Int = 0
    private var spentMandatory: Int = 0
    private var spentOptional: Int = 0

    data class ShopItem(
        val name: String,
        val price: Int,
        val category: String,
        val effect: String,
        val explanation: String
    )

    private val shopItems = listOf(
        ShopItem("🥩 Корм на день", 100, "mandatory", "Сытость +30", "Финни нуждается в еде каждый день."),
        ShopItem("💧 Свежая вода", 50, "mandatory", "Здоровье +15", "Вода — это жизнь!"),
        ShopItem("🛁 Шампунь и уход", 80, "mandatory", "Настроение +20, Здоровье +10", "Чистота — залог здоровья!"),
        ShopItem("🩺 Визит к ветеринару", 150, "mandatory", "Здоровье +40", "Лучше предотвратить, чем лечить!"),
        ShopItem("️ Замена подстилки", 120, "mandatory", "Энергия +25, Настроение +15", "Чистое место для сна важно."),
        ShopItem("🎾 Игрушка-пищалка", 200, "optional", "Настроение +40", "Игрушка — это весело!"),
        ShopItem("🎀 Бантик или ошейник", 150, "optional", "Настроение +30", "Аксессуары украшают Финни!"),
        ShopItem("🍪 Лакомство", 90, "optional", "Настроение +20", "Вкусняшка для радости!"),
        ShopItem("🎡 Поход в парк", 250, "optional", "Настроение +50, Энергия -10", "Развлечение на целый день!"),
        ShopItem("📸 Фотосессия", 180, "optional", "Настроение +25", "Память на всю жизнь!")
    )

    private var currentFilter = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        loadData()
        setupViews()
        renderShopItems()
    }

    private fun loadData() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        balance = prefs.getInt("balance", 1000)
        planMandatory = prefs.getInt("plan_mandatory", 0) // Исправлено: было 3, но сохраняется сумма
        planOptional = prefs.getInt("plan_optional", 0)
        spentMandatory = prefs.getInt("spent_mandatory", 0)
        spentOptional = prefs.getInt("spent_optional", 0)
    }

    private fun saveData() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        prefs.edit().apply {
            putInt("balance", balance)
            putInt("spent_mandatory", spentMandatory)
            putInt("spent_optional", spentOptional)
            apply()
        }
    }

    private fun setupViews() {
        val tvBalance = findViewById<TextView>(R.id.tv_balance)
        val tvPlanMandatory = findViewById<TextView>(R.id.tv_plan_mandatory)
        val tvPlanOptional = findViewById<TextView>(R.id.tv_plan_optional)
        val pbMandatory = findViewById<ProgressBar>(R.id.pb_mandatory)
        val pbOptional = findViewById<ProgressBar>(R.id.pb_optional)

        val btnFilterAll = findViewById<Button>(R.id.btn_filter_all)
        val btnFilterMandatory = findViewById<Button>(R.id.btn_filter_mandatory)
        val btnFilterOptional = findViewById<Button>(R.id.btn_filter_optional)

        tvBalance.text = "💰 $balance"
        tvPlanMandatory.text = "$spentMandatory из $planMandatory"
        tvPlanOptional.text = "$spentOptional из $planOptional"

        pbMandatory.max = if (planMandatory > 0) planMandatory else 100
        pbMandatory.progress = spentMandatory
        pbOptional.max = if (planOptional > 0) planOptional else 100
        pbOptional.progress = spentOptional

        pbMandatory.progressTintList = android.content.res.ColorStateList.valueOf(0xFF66BB6A.toInt())
        pbOptional.progressTintList = android.content.res.ColorStateList.valueOf(0xFFFF9800.toInt())

        btnFilterAll.setOnClickListener {
            currentFilter = "all"
            updateFilterButtons(btnFilterAll, btnFilterMandatory, btnFilterOptional)
            renderShopItems()
        }

        btnFilterMandatory.setOnClickListener {
            currentFilter = "mandatory"
            updateFilterButtons(btnFilterMandatory, btnFilterAll, btnFilterOptional)
            renderShopItems()
        }

        btnFilterOptional.setOnClickListener {
            currentFilter = "optional"
            updateFilterButtons(btnFilterOptional, btnFilterAll, btnFilterMandatory)
            renderShopItems()
        }

        updateFilterButtons(btnFilterAll, btnFilterMandatory, btnFilterOptional)
    }

    // ИСПРАВЛЕНИЕ: Неактивные кнопки теперь белые с серым текстом
    private fun updateFilterButtons(active: Button, vararg inactive: Button) {
        val fixedColor = android.content.res.ColorStateList.valueOf(0xFFC9A7E8.toInt())
        val whiteColor = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())

        // Активная кнопка
        active.backgroundTintList = fixedColor
        active.setTextColor(0xFFFFFFFF.toInt())
        active.isAllCaps = false

        // Неактивные кнопки
        inactive.forEach {
            it.backgroundTintList = whiteColor
            it.setTextColor(0xFF7F8C8D.toInt()) // Серый текст
            it.isAllCaps = false
        }
    }

    private fun renderShopItems() {
        val container = findViewById<LinearLayout>(R.id.shop_items_container)
        container.removeAllViews()

        val filteredItems = when (currentFilter) {
            "mandatory" -> shopItems.filter { it.category == "mandatory" }
            "optional" -> shopItems.filter { it.category == "optional" }
            else -> shopItems
        }

        for (item in filteredItems) {
            val itemCard = createItemCard(item)
            container.addView(itemCard)
        }
    }

    private fun createItemCard(item: ShopItem): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.background = getDrawable(R.drawable.bg_card)
        card.setPadding(32, 24, 32, 24)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 16)
        card.layoutParams = layoutParams

        val nameView = TextView(this)
        nameView.text = item.name
        nameView.textSize = 18f
        nameView.setTextColor(0xFF2C3E50.toInt())
        nameView.setTypeface(null, android.graphics.Typeface.BOLD)
        card.addView(nameView)

        val categoryView = TextView(this)
        categoryView.text = if (item.category == "mandatory") "✅ Обязательное" else "⭐ Необязательное"
        categoryView.textSize = 14f
        categoryView.setTextColor(if (item.category == "mandatory") 0xFF66BB6A.toInt() else 0xFFFF9800.toInt())
        categoryView.setTypeface(null, android.graphics.Typeface.BOLD)
        card.addView(categoryView)

        val effectView = TextView(this)
        effectView.text = "Эффект: ${item.effect}"
        effectView.textSize = 14f
        effectView.setTextColor(0xFF7F8C8D.toInt())
        effectView.setPadding(0, 12, 0, 12)
        card.addView(effectView)

        val bottomRow = LinearLayout(this)
        bottomRow.orientation = LinearLayout.HORIZONTAL
        bottomRow.gravity = android.view.Gravity.CENTER_VERTICAL

        val priceView = TextView(this)
        priceView.text = "${item.price} монет"
        priceView.textSize = 18f
        priceView.setTextColor(0xFF2C3E50.toInt())
        priceView.setTypeface(null, android.graphics.Typeface.BOLD)
        priceView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        bottomRow.addView(priceView)

        val buyButton = androidx.appcompat.widget.AppCompatButton(this)
        buyButton.text = "Купить"
        buyButton.textSize = 14f
        buyButton.isAllCaps = false

        buyButton.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFC9A7E8.toInt())

        // ИСПРАВЛЕНИЕ: убрана лишняя буква 'a' в HEX коде цвета
        buyButton.setTextColor(0xFFFFFFFF.toInt())

        buyButton.setPadding(32, 16, 32, 16)

        buyButton.setOnClickListener {
            showPurchaseDialog(item)
        }
        bottomRow.addView(buyButton)

        card.addView(bottomRow)

        return card
    }

    private fun showPurchaseDialog(item: ShopItem) {
        if (balance < item.price) {
            Toast.makeText(this, "❌ Не хватает ${item.price - balance} монет!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Подтверждение покупки")
            .setMessage("${item.name}\n\nЦена: ${item.price} монет\nОстанется: ${balance - item.price} монет")
            .setPositiveButton("Купить") { _, _ ->
                completePurchase(item)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun completePurchase(item: ShopItem) {
        balance -= item.price

        if (item.category == "mandatory") {
            spentMandatory += item.price
        } else {
            spentOptional += item.price
        }

        saveData()

        val message = if (item.category == "mandatory") {
            "✅ Правильно! Ты купил ${item.name} — это обязательный расход.\n\n${item.explanation}"
        } else {
            "⭐ Покупка совершена! ${item.name} — это необязательный расход.\n\n${item.explanation}"
        }

        AlertDialog.Builder(this)
            .setTitle("Покупка завершена!")
            .setMessage(message)
            .setPositiveButton("Отлично") { _, _ ->
                finish()
            }
            .show()
    }
}
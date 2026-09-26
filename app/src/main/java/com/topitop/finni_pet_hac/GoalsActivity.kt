package com.topitop.finni_pet_hac

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalsActivity : AppCompatActivity() {

    private var balance: Int = 0
    private var currentGoalId: String? = null
    private var goalProgress: Int = 0

    data class Goal(
        val id: String,
        val icon: String,
        val name: String,
        val cost: Int
    )

    private val availableGoals = listOf(
        Goal("bike", "🚲", "Велосипед", 2000),
        Goal("house", "🏠", "Новый домик", 1500),
        Goal("crown", "👑", "Корона чемпиона", 2500)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        loadState()
        setupUI()
    }

    private fun loadState() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        balance = prefs.getInt("balance", 1000)
        currentGoalId = prefs.getString("current_goal_id", null)
        goalProgress = prefs.getInt("goal_progress", 0)
    }

    private fun saveState() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        prefs.edit().apply {
            putInt("balance", balance)
            putString("current_goal_id", currentGoalId)
            putInt("goal_progress", goalProgress)

            // Добавляем сохранение для главного экрана
            if (currentGoalId != null) {
                val goal = availableGoals.find { it.id == currentGoalId }
                if (goal != null) {
                    putString("goal_name", goal.name)
                    putInt("goal_target", goal.cost)
                }
            } else {
                putString("goal_name", "")
                putInt("goal_target", 0)
            }

            apply()
        }
    }

    private fun setupUI() {
        findViewById<TextView>(R.id.tv_goals_balance).text = "💰 $balance"

        val cardCurrent = findViewById<LinearLayout>(R.id.card_current_goal)
        val listContainer = findViewById<LinearLayout>(R.id.goals_list_container)

        if (currentGoalId != null) {
            val goal = availableGoals.find { it.id == currentGoalId }
            if (goal != null) {
                cardCurrent.visibility = LinearLayout.VISIBLE
                findViewById<TextView>(R.id.tv_goal_icon).text = goal.icon
                findViewById<TextView>(R.id.tv_goal_name).text = goal.name

                val percent = (goalProgress.toFloat() / goal.cost * 100).toInt().coerceIn(0, 100)
                findViewById<TextView>(R.id.tv_goal_progress).text = "$goalProgress из ${goal.cost} монет ($percent%)"

                val pb = findViewById<ProgressBar>(R.id.pb_goal)
                pb.max = goal.cost
                pb.progress = goalProgress

                findViewById<Button>(R.id.btn_add_to_goal).setOnClickListener { showAddDialog(goal) }
                findViewById<Button>(R.id.btn_withdraw_from_goal).setOnClickListener { showWithdrawDialog(goal) }
            }
        } else {
            cardCurrent.visibility = LinearLayout.GONE
        }

        // Рендерим список для выбора
        listContainer.removeAllViews()
        for (goal in availableGoals) {
            val isSelected = goal.id == currentGoalId
            val btn = Button(this)
            btn.text = "${goal.icon} ${goal.name} (${goal.cost} монет)"
            btn.textSize = 16f
            btn.isAllCaps = false
            btn.setPadding(24, 16, 24, 16)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 12)
            btn.layoutParams = params

            if (isSelected) {
                btn.background = getDrawable(R.drawable.bg_block_purple)
                btn.setTextColor(0xFFFFFFFF.toInt())
            } else {
                btn.background = getDrawable(R.drawable.bg_card)
                btn.setTextColor(0xFF2C3E50.toInt())
                btn.setOnClickListener { selectGoal(goal) }
            }

            listContainer.addView(btn)
        }
    }

    private fun selectGoal(goal: Goal) {
        AlertDialog.Builder(this)
            .setTitle("Выбрать эту цель?")
            .setMessage("Ты будешь копить на ${goal.name} за ${goal.cost} монет.")
            .setPositiveButton("Да") { _, _ ->
                currentGoalId = goal.id
                goalProgress = 0
                saveState()
                setupUI()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun showAddDialog(goal: Goal) {
        val input = EditText(this)
        input.hint = "Сколько отложить?"
        input.textSize = 18f
        input.setPadding(48, 32, 48, 32)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.background = getDrawable(R.drawable.bg_card)
        input.setTextColor(0xFF2C3E50.toInt())

        AlertDialog.Builder(this)
            .setTitle("Пополнить копилку")
            .setMessage("Доступно: $balance монет")
            .setView(input)
            .setPositiveButton("Отложить") { _, _ ->
                val amount = input.text.toString().toIntOrNull() ?: 0
                if (amount > 0 && amount <= balance) {
                    balance -= amount
                    goalProgress += amount
                    saveState()
                    Toast.makeText(this, "Отложено $amount монет!", Toast.LENGTH_SHORT).show()

                    if (goalProgress >= goal.cost) {
                        showRewardDialog(goal)
                    } else {
                        setupUI()
                    }
                } else {
                    Toast.makeText(this, "Некорректная сумма!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showWithdrawDialog(goal: Goal) {
        if (goalProgress <= 0) {
            Toast.makeText(this, "Копилка пуста!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Снять с накоплений")
            .setMessage("В копилке сейчас $goalProgress монет.\nЕсли снимешь, цель отодвинется дальше.")
            .setPositiveButton("Снять всё") { _, _ ->
                balance += goalProgress
                goalProgress = 0
                saveState()
                Toast.makeText(this, "Монеты возвращены на баланс!", Toast.LENGTH_SHORT).show()
                setupUI()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showRewardDialog(goal: Goal) {
        AlertDialog.Builder(this)
            .setTitle("🎉 Ура! Цель достигнута!")
            .setMessage("Ты накопил на ${goal.name}!\nФинни очень гордится твоим терпением!")
            .setPositiveButton("Забрать награду") { _, _ ->
                currentGoalId = null
                goalProgress = 0

                // Очищаем данные для главного экрана
                val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
                prefs.edit().apply {
                    putString("goal_name", "")
                    putInt("goal_target", 0)
                    putInt("goal_progress", 0)
                    apply()
                }

                setupUI()
            }
            .show()
    }
}
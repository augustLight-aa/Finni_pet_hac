package com.topitop.finni_pet_hac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var balance: Int = 2156
    private var savings: Int = 2740
    private var currentPeriod: Int = 1
    private val maxPeriods: Int = 5
    private var petImageName: String = "pet_fox_orange"
    private var petName: String = "Финни"
    private var goalName: String = ""
    private var goalProgress: Int = 0
    private var goalTarget: Int = 100

    // Состояние питомца (4 показателя)
    private var petMood: Int = 70
    private var petHealth: Int = 80
    private var petHunger: Int = 60
    private var petEnergy: Int = 65

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            startActivity(Intent(this, CreatePetActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        loadPetData()
        setupViews()
    }

    private fun loadPetData() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        petName = prefs.getString("pet_name", "Финни") ?: "Финни"
        petImageName = prefs.getString("pet_image", "pet_fox_orange") ?: "pet_fox_orange"
        balance = prefs.getInt("balance", 2156)
        savings = prefs.getInt("savings", 2740)
        currentPeriod = prefs.getInt("current_period", 1)
        goalName = prefs.getString("goal_name", "") ?: ""
        goalProgress = prefs.getInt("goal_progress", 0)
        goalTarget = prefs.getInt("goal_target", 100)
        petMood = prefs.getInt("pet_mood", 70)
        petHealth = prefs.getInt("pet_health", 80)
        petHunger = prefs.getInt("pet_hunger", 60)
        petEnergy = prefs.getInt("pet_energy", 65)
    }

    private fun savePetData() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        prefs.edit().apply {
            putString("pet_name", petName)
            putString("pet_image", petImageName)
            putInt("balance", balance)
            putInt("savings", savings)
            putInt("current_period", currentPeriod)
            putString("goal_name", goalName)
            putInt("goal_progress", goalProgress)
            putInt("goal_target", goalTarget)
            putInt("pet_mood", petMood)
            putInt("pet_health", petHealth)
            putInt("pet_hunger", petHunger)
            putInt("pet_energy", petEnergy)
            apply()
        }
    }

    private fun setupViews() {
        // Основные элементы
        val tvBalance = findViewById<TextView>(R.id.tv_balance)
        val tvSavings = findViewById<TextView>(R.id.tv_savings)
        val tvPetName = findViewById<TextView>(R.id.tv_pet_name)
        val tvPeriod = findViewById<TextView>(R.id.tv_period)

        // Состояние питомца
        val tvMoodText = findViewById<TextView>(R.id.tv_pet_mood_text)
        val pbMood = findViewById<ProgressBar>(R.id.pb_pet_mood)
        val tvHealthText = findViewById<TextView>(R.id.tv_pet_health_text)
        val pbHealth = findViewById<ProgressBar>(R.id.pb_pet_health)
        val tvHungerText = findViewById<TextView>(R.id.tv_pet_hunger_text)
        val pbHunger = findViewById<ProgressBar>(R.id.pb_pet_hunger)
        val tvEnergyText = findViewById<TextView>(R.id.tv_pet_energy_text)
        val pbEnergy = findViewById<ProgressBar>(R.id.pb_pet_energy)

        // Цель
        val tvGoalName = findViewById<TextView>(R.id.tv_goal_name)
        val pbGoal = findViewById<ProgressBar>(R.id.pb_goal)
        val btnSelectGoal = findViewById<Button>(R.id.btn_select_goal)

        // Текущее задание (упрощенное)
        val tvTaskName = findViewById<TextView>(R.id.tv_task_name)
        val btnDoTask = findViewById<Button>(R.id.btn_do_task)

        // Питомец
        val ivMainPet = findViewById<ImageView>(R.id.iv_main_pet)
        val ivMainPetSmall = findViewById<ImageView>(R.id.iv_main_pet_small)

        // Кнопки
        val btnNextPeriod = findViewById<Button>(R.id.btn_next_period)
        val btnSettings = findViewById<ImageButton>(R.id.btn_settings)
        val btnNavHome = findViewById<ImageButton>(R.id.btn_nav_home)
        val btnNavBudget = findViewById<ImageButton>(R.id.btn_nav_budget)
        val btnNavTasks = findViewById<ImageButton>(R.id.btn_nav_tasks)
        val btnNavShop = findViewById<ImageButton>(R.id.btn_nav_shop)
        val btnNavGoals = findViewById<ImageButton>(R.id.btn_nav_goals)

        // Обновляем UI
        updateUI(
            tvBalance, tvSavings, tvPetName, tvPeriod,
            tvMoodText, pbMood, tvHealthText, pbHealth,
            tvHungerText, pbHunger, tvEnergyText, pbEnergy,
            tvGoalName, pbGoal, btnSelectGoal,
            tvTaskName, btnDoTask,
            ivMainPet, ivMainPetSmall
        )

        // Кнопка настроек
        btnSettings.setOnClickListener {
            startActivity(Intent(this, AdultActivity::class.java))
        }

        // Навигация
        btnNavHome.setOnClickListener {
            Toast.makeText(this, "Вы уже на главном экране", Toast.LENGTH_SHORT).show()
        }
        btnNavBudget.setOnClickListener { startActivity(Intent(this, BudgetActivity::class.java)) }
        btnNavTasks.setOnClickListener { startActivity(Intent(this, TasksActivity::class.java)) }
        btnNavShop.setOnClickListener { startActivity(Intent(this, ShopActivity::class.java)) }
        btnNavGoals.setOnClickListener { startActivity(Intent(this, GoalsActivity::class.java)) }

        // Кнопка "Выполнить задание"
        btnDoTask.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }

        // Переход к следующему периоду
        btnNextPeriod.setOnClickListener {
            if (currentPeriod < maxPeriods) {
                currentPeriod++
                balance += 1000

                // Показатели уменьшаются со временем
                petHunger = (petHunger - 20).coerceAtLeast(0)
                petEnergy = (petEnergy - 15).coerceAtLeast(0)
                petMood = (petMood - 10).coerceAtLeast(0)
                petHealth = (petHealth - 5).coerceAtLeast(0)

                savePetData()
                updateUI(
                    tvBalance, tvSavings, tvPetName, tvPeriod,
                    tvMoodText, pbMood, tvHealthText, pbHealth,
                    tvHungerText, pbHunger, tvEnergyText, pbEnergy,
                    tvGoalName, pbGoal, btnSelectGoal,
                    tvTaskName, btnDoTask,
                    ivMainPet, ivMainPetSmall
                )
                Toast.makeText(this, "Новый период! +1000 монет. Питомец проголодался!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Это последний период!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(
        tvBalance: TextView, tvSavings: TextView, tvPetName: TextView, tvPeriod: TextView,
        tvMoodText: TextView, pbMood: ProgressBar,
        tvHealthText: TextView, pbHealth: ProgressBar,
        tvHungerText: TextView, pbHunger: ProgressBar,
        tvEnergyText: TextView, pbEnergy: ProgressBar,
        tvGoalName: TextView, pbGoal: ProgressBar, btnSelectGoal: Button,
        tvTaskName: TextView, btnDoTask: Button, // Убрали tvTaskTopic и tvTaskReward
        ivMainPet: ImageView, ivMainPetSmall: ImageView
    ) {
        tvBalance.text = "$balance монет"
        tvSavings.text = "$savings монет"
        tvPetName.text = petName
        tvPeriod.text = "Период: $currentPeriod из $maxPeriods"

        // Настроение
        pbMood.progress = petMood
        tvMoodText.text = getStatusText(petMood)

        // Здоровье
        pbHealth.progress = petHealth
        tvHealthText.text = getStatusText(petHealth)

        // Сытость
        pbHunger.progress = petHunger
        tvHungerText.text = getStatusText(petHunger)

        // Энергия
        pbEnergy.progress = petEnergy
        tvEnergyText.text = getStatusText(petEnergy)

        // Цель
        if (goalName.isNotEmpty() && goalName != "Не выбрана") {
            tvGoalName.visibility = TextView.VISIBLE
            pbGoal.visibility = ProgressBar.VISIBLE
            btnSelectGoal.visibility = Button.GONE
            tvGoalName.text = goalName
            pbGoal.max = goalTarget
            pbGoal.progress = goalProgress
        } else {
            tvGoalName.visibility = TextView.GONE
            pbGoal.visibility = ProgressBar.GONE
            btnSelectGoal.visibility = Button.VISIBLE

            btnSelectGoal.setOnClickListener {
                startActivity(Intent(this, GoalsActivity::class.java))
            }
        }

        // Текущее задание (первое невыполненное)
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        val completedTasks = prefs.getStringSet("completed_tasks", emptySet()) ?: emptySet()

        val tasks = listOf(
            Triple("Первый доход", "Планирование", 40),
            Triple("Непредвиденная трата", "Покупки", 50),
            Triple("Большая цель", "Накопления", 60),
            Triple("Сравнение цен", "Покупки", 30),
            Triple("План на неделю", "Планирование", 40),
            Triple("Копилка растет", "Накопления", 50)
        )

        val currentTask = tasks.firstOrNull { it.first !in completedTasks }

        if (currentTask != null) {
            tvTaskName.visibility = TextView.VISIBLE
            btnDoTask.visibility = Button.VISIBLE
            // Объединили название и награду в одну строку для компактности
            tvTaskName.text = "📌 ${currentTask.first} (+${currentTask.third} монет)"
        } else {
            tvTaskName.visibility = TextView.GONE
            btnDoTask.visibility = Button.GONE
        }

        // Картинки питомца
        val resId = resources.getIdentifier(petImageName, "drawable", packageName)
        if (resId != 0) {
            ivMainPet.setImageResource(resId)
            ivMainPetSmall.setImageResource(resId)
        }
    }

    private fun getStatusText(value: Int): String {
        return when {
            value >= 70 -> "хорошее"
            value >= 40 -> "среднее"
            else -> "низкое"
        }
    }

    override fun onResume() {
        super.onResume()
        loadPetData()

        val tvBalance = findViewById<TextView>(R.id.tv_balance)
        val tvSavings = findViewById<TextView>(R.id.tv_savings)
        val tvPetName = findViewById<TextView>(R.id.tv_pet_name)
        val tvPeriod = findViewById<TextView>(R.id.tv_period)
        val tvMoodText = findViewById<TextView>(R.id.tv_pet_mood_text)
        val pbMood = findViewById<ProgressBar>(R.id.pb_pet_mood)
        val tvHealthText = findViewById<TextView>(R.id.tv_pet_health_text)
        val pbHealth = findViewById<ProgressBar>(R.id.pb_pet_health)
        val tvHungerText = findViewById<TextView>(R.id.tv_pet_hunger_text)
        val pbHunger = findViewById<ProgressBar>(R.id.pb_pet_hunger)
        val tvEnergyText = findViewById<TextView>(R.id.tv_pet_energy_text)
        val pbEnergy = findViewById<ProgressBar>(R.id.pb_pet_energy)
        val tvGoalName = findViewById<TextView>(R.id.tv_goal_name)
        val pbGoal = findViewById<ProgressBar>(R.id.pb_goal)
        val btnSelectGoal = findViewById<Button>(R.id.btn_select_goal)

        // Упрощенные переменные задания
        val tvTaskName = findViewById<TextView>(R.id.tv_task_name)
        val btnDoTask = findViewById<Button>(R.id.btn_do_task)

        val ivMainPet = findViewById<ImageView>(R.id.iv_main_pet)
        val ivMainPetSmall = findViewById<ImageView>(R.id.iv_main_pet_small)

        updateUI(
            tvBalance, tvSavings, tvPetName, tvPeriod,
            tvMoodText, pbMood, tvHealthText, pbHealth,
            tvHungerText, pbHunger, tvEnergyText, pbEnergy,
            tvGoalName, pbGoal, btnSelectGoal,
            tvTaskName, btnDoTask,
            ivMainPet, ivMainPetSmall
        )
    }
}
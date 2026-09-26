package com.topitop.finni_pet_hac

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TasksActivity : AppCompatActivity() {

    private var balance: Int = 0
    private var petMood: Int = 0
    private var currentFilter: String = "all"

    data class Task(
        val title: String,
        val topic: String,
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val reward: Int,
        val explanationCorrect: String,
        val explanationWrong: String
    )

    private val tasks = listOf(
        Task("Первый доход", "Планирование", "Ты получил 600 монет карманных денег. Что лучше сделать?", listOf("Потратить все на игрушки", "Разделить на нужное, желаемое и накопления"), 1, 40, "Верно! Сначала выдели деньги на обязательные расходы, часть отложи, а остальное оставь на желания.", "Не совсем. Если потратить всё сразу, на еду и уход за питомцем может не хватить."),
        Task("Непредвиденная трата", "Покупки", "Финни заболел, и нужно купить лекарства за 200 монет. Но ты уже потратил всё на игрушки. Что делать?", listOf("Ничего не делать, пусть питомец подождет", "Отложить покупку новой игрушки и купить лекарства"), 1, 50, "Правильно! Здоровье питомца — это обязательный расход. Его нельзя откладывать.", "Так делать нельзя. Обязательные расходы всегда важнее развлечений."),
        Task("Большая цель", "Накопления", "Ты хочешь купить Финни новый домик за 2000 монет. У тебя есть 500. Как быть?", listOf("Откладывать по 100 монет каждый период", "Потратить 500 на лакомства, а домик купить потом"), 0, 60, "Отлично! Регулярные небольшие накопления — лучший способ достичь большой цели.", "Если потратить всё сейчас, цель отодвинется на очень долгое время."),
        Task("Сравнение цен", "Покупки", "В одном магазине корм стоит 100 монет, в другом — 80 монет. Оба одинакового качества. Где купить?", listOf("Где дороже — значит лучше", "Где дешевле — сэкономлю 20 монет"), 1, 30, "Правильно! Если качество одинаковое, нет смысла переплачивать.", "Высокая цена не всегда означает лучшее качество. Сравнивай товары!"),
        Task("План на неделю", "Планирование", "У тебя 400 монет на неделю. Еда стоит 200, игрушки — 150, накопления — 50. Хватит ли?", listOf("Да, 200 + 150 + 50 = 400, всё сходится", "Нет, нужно больше денег"), 0, 40, "Верно! Ты правильно посчитал и спланировал бюджет.", "Давай посчитаем: 200 + 150 + 50 = 400. Это ровно твой бюджет!"),
        Task("Копилка растет", "Накопления", "Ты откладываешь по 100 монет каждый период. Цель — 500 монет. Через сколько периодов накопишь?", listOf("Через 3 периода", "Через 5 периодов"), 1, 50, "Правильно! 100 × 5 = 500. Регулярные накопления работают!", "Посчитаем: 100 × 3 = 300, этого мало. Нужно 5 периодов: 100 × 5 = 500.")
    )

    private val completedTasks = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        balance = prefs.getInt("balance", 1000)
        petMood = prefs.getInt("pet_mood", 70)

        findViewById<TextView>(R.id.tv_tasks_balance).text = "💰 $balance"

        setupFilters()
        renderTasks()
    }

    private fun setupFilters() {
        val btnAll = findViewById<Button>(R.id.btn_filter_all)

        val purpleColor = android.content.res.ColorStateList.valueOf(0xFFC9A7E8.toInt())
        val whiteColor = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())

        // Функция для обновления состояния кнопок
        fun updateFilters(activeBtn: Button, vararg inactiveBtns: Button) {
            activeBtn.backgroundTintList = purpleColor
            activeBtn.setTextColor(0xFFFFFFFF.toInt())

            inactiveBtns.forEach { btn ->
                btn.backgroundTintList = whiteColor
                btn.setTextColor(0xFF7F8C8D.toInt())
            }
        }

        btnAll.setOnClickListener {
            currentFilter = "all"
            updateFilters(btnAll)
            renderTasks()
        }

        // Начальное состояние
        updateFilters(btnAll)
    }

    private fun renderTasks() {
        val container = findViewById<LinearLayout>(R.id.tasks_container)
        container.removeAllViews()

        val filteredTasks = if (currentFilter == "all") {
            tasks
        } else {
            tasks.filter { it.topic == currentFilter }
        }

        var completedCount = 0
        for ((index, task) in tasks.withIndex()) {
            if (completedTasks.contains(index)) completedCount++

            // Рендерим только если задание попадает под фильтр
            if (currentFilter == "all" || task.topic == currentFilter) {
                val isCompleted = completedTasks.contains(index)
                container.addView(createTaskCard(task, index, isCompleted))
            }
        }

        // Обновляем прогресс-бар
        findViewById<TextView>(R.id.tv_tasks_completed).text = "$completedCount из ${tasks.size}"
        val pb = findViewById<ProgressBar>(R.id.pb_tasks_progress)
        pb.max = tasks.size
        pb.progress = completedCount
    }

    private fun createTaskCard(task: Task, index: Int, isCompleted: Boolean): LinearLayout {
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

        // 1. Название задания
        val titleView = TextView(this)
        titleView.text = task.title
        titleView.textSize = 18f
        titleView.setTextColor(0xFF2C3E50.toInt())
        titleView.setTypeface(null, android.graphics.Typeface.BOLD)
        card.addView(titleView)

        // 2. Тема (как категория в магазине)
        val topicView = TextView(this)
        topicView.text = "Тема: ${task.topic}"
        topicView.textSize = 14f
        topicView.setTextColor(0xFF7F8C8D.toInt())
        topicView.setPadding(0, 4, 0, 12)
        card.addView(topicView)

        // 3. Нижняя строка: Награда + Кнопка
        val bottomRow = LinearLayout(this)
        bottomRow.orientation = LinearLayout.HORIZONTAL
        bottomRow.gravity = android.view.Gravity.CENTER_VERTICAL

        val rewardView = TextView(this)
        rewardView.text = "Награда: ${task.reward} монет"
        rewardView.textSize = 16f
        rewardView.setTextColor(0xFF2980B9.toInt())
        rewardView.setTypeface(null, android.graphics.Typeface.BOLD)
        rewardView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        bottomRow.addView(rewardView)

        val actionButton = androidx.appcompat.widget.AppCompatButton(this)
        actionButton.text = if (isCompleted) "Выполнено ✅" else "Выполнить"
        actionButton.textSize = 14f
        actionButton.isAllCaps = false
        actionButton.isEnabled = !isCompleted

        if (isCompleted) {
            actionButton.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
            actionButton.setTextColor(0xFF7F8C8D.toInt())
        } else {
            // Жестко фиксируем светло-фиолетовый цвет, игнорируя тему
            actionButton.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFC9A7E8.toInt())
            actionButton.setTextColor(0xFFFFFFFF.toInt())
            actionButton.setOnClickListener { showTaskDialog(task, index) }
        }
        bottomRow.addView(actionButton)
        card.addView(bottomRow)

        return card
    }

    private fun showTaskDialog(task: Task, index: Int) {
        val dialogView = LinearLayout(this)
        dialogView.orientation = LinearLayout.VERTICAL
        dialogView.setPadding(48, 32, 48, 32)

        val questionText = TextView(this)
        questionText.text = task.question
        questionText.textSize = 16f
        questionText.setTextColor(0xFF2C3E50.toInt())
        questionText.setPadding(0, 0, 0, 24)
        dialogView.addView(questionText)

        for ((optionIndex, option) in task.options.withIndex()) {
            val optionButton = androidx.appcompat.widget.AppCompatButton(this)
            optionButton.text = option
            optionButton.textSize = 14f
            optionButton.isAllCaps = false
            optionButton.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
            optionButton.setTextColor(0xFF2980B9.toInt())
            optionButton.setPadding(24, 16, 24, 16)

            val buttonParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            buttonParams.setMargins(0, 0, 0, 12)
            optionButton.layoutParams = buttonParams

            optionButton.setOnClickListener {
                handleTaskAnswer(task, index, optionIndex)
            }
            dialogView.addView(optionButton)
        }

        AlertDialog.Builder(this)
            .setTitle(task.title)
            .setView(dialogView)
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun handleTaskAnswer(task: Task, index: Int, chosenIndex: Int) {
        val isCorrect = chosenIndex == task.correctIndex
        val explanation = if (isCorrect) task.explanationCorrect else task.explanationWrong

        if (isCorrect) {
            balance += task.reward
            petMood += 10
            completedTasks.add(index)

            val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
            val completedSet = prefs.getStringSet("completed_tasks", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            completedSet.add(task.title)
            prefs.edit().putStringSet("completed_tasks", completedSet).apply()

            prefs.edit().apply {
                putInt("balance", balance)
                putInt("pet_mood", petMood)
                apply()
            }
        }

        AlertDialog.Builder(this)
            .setTitle(if (isCorrect) "Верно! 🎉" else "Не совсем 🤔")
            .setMessage("$explanation\n\nТы получил ${task.reward} монет!")
            .setPositiveButton("Отлично") { _, _ ->
                findViewById<TextView>(R.id.tv_tasks_balance).text = "💰 $balance"
                renderTasks() // Перерисовываем, чтобы обновить прогресс и кнопку
            }
            .show()
    }
}
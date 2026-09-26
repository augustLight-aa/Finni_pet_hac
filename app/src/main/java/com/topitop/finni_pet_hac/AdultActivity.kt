package com.topitop.finni_pet_hac

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adult)

        val layoutBarrier = findViewById<LinearLayout>(R.id.layout_barrier)
        val layoutContent = findViewById<LinearLayout>(R.id.layout_adult_content)
        val etMathAnswer = findViewById<EditText>(R.id.et_math_answer)
        val btnCheckAnswer = findViewById<Button>(R.id.btn_check_answer)
        val btnResetProfile = findViewById<Button>(R.id.btn_reset_profile)
        val tvStatPeriod = findViewById<TextView>(R.id.tv_stat_period)
        val tvStatBalance = findViewById<TextView>(R.id.tv_stat_balance)
        val tvStatMood = findViewById<TextView>(R.id.tv_stat_mood)
        val tvStatGoal = findViewById<TextView>(R.id.tv_stat_goal)

        // Проверка ответа на пример
        btnCheckAnswer.setOnClickListener {
            val answer = etMathAnswer.text.toString().toIntOrNull()
            if (answer == 15) { // 7 + 8 = 15
                layoutBarrier.visibility = LinearLayout.GONE
                layoutContent.visibility = LinearLayout.VISIBLE
                loadStats(tvStatPeriod, tvStatBalance, tvStatMood, tvStatGoal)
            } else {
                Toast.makeText(this, "Неверный ответ. Попробуйте еще раз.", Toast.LENGTH_SHORT).show()
            }
        }

        // Сброс профиля
        btnResetProfile.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Сбросить профиль?")
                .setMessage("Весь прогресс питомца, баланс и накопления будут удалены. Это действие нельзя отменить.")
                .setPositiveButton("Да, сбросить") { _, _ ->
                    resetProfile()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun loadStats(tvPeriod: TextView, tvBalance: TextView, tvMood: TextView, tvGoal: TextView) {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        val period = prefs.getInt("current_period", 1)
        val balance = prefs.getInt("balance", 1000)
        val mood = prefs.getInt("pet_mood", 70)
        val goalName = prefs.getString("goal_name", "Не выбрана") ?: "Не выбрана"

        tvPeriod.text = "$period из 5"
        tvBalance.text = "$balance"
        tvMood.text = "$mood/100"
        tvGoal.text = goalName
    }

    private fun resetProfile() {
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        prefs.edit().clear().apply()

        Toast.makeText(this, "Профиль успешно сброшен!", Toast.LENGTH_SHORT).show()

        // Перезапуск приложения на экран создания питомца
        val intent = Intent(this, CreatePetActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
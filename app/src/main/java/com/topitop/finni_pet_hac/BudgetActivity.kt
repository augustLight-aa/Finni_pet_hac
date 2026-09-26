package com.topitop.finni_pet_hac

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetActivity : AppCompatActivity() {

    private var totalBalance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        // Загружаем текущий баланс
        val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
        totalBalance = prefs.getInt("balance", 1000)

        val tvBalance = findViewById<TextView>(R.id.tv_balance)
        val etMandatory = findViewById<EditText>(R.id.et_mandatory)
        val etOptional = findViewById<EditText>(R.id.et_optional)
        val etSavings = findViewById<EditText>(R.id.et_savings)
        val tvRemaining = findViewById<TextView>(R.id.tv_remaining)
        val btnConfirm = findViewById<Button>(R.id.btn_confirm_budget)

        tvBalance.text = "💰 $totalBalance"

        // Функция пересчета остатка
        val updateRemaining = {
            val mandatory = etMandatory.text.toString().toIntOrNull() ?: 0
            val optional = etOptional.text.toString().toIntOrNull() ?: 0
            val savings = etSavings.text.toString().toIntOrNull() ?: 0

            val spent = mandatory + optional + savings
            val remaining = totalBalance - spent

            tvRemaining.text = "Остаток: $remaining монет"

            // Цветовая индикация остатка
            when {
                remaining < 0 -> {
                    tvRemaining.setTextColor(0xFFF44336.toInt()) // Красный (перебор)
                    btnConfirm.isEnabled = false
                    btnConfirm.alpha = 0.5f
                }
                remaining == 0 -> {
                    tvRemaining.setTextColor(0xFF66BB6A.toInt()) // Зеленый (идеально!)
                    btnConfirm.isEnabled = true
                    btnConfirm.alpha = 1f
                }
                else -> {
                    tvRemaining.setTextColor(0xFF2980B9.toInt()) // Синий (осталось)
                    btnConfirm.isEnabled = true
                    btnConfirm.alpha = 1f
                }
            }
        }

        // Слушатели изменений в полях ввода
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRemaining()
            }
        }

        etMandatory.addTextChangedListener(textWatcher)
        etOptional.addTextChangedListener(textWatcher)
        etSavings.addTextChangedListener(textWatcher)

        // Кнопка подтверждения
        btnConfirm.setOnClickListener {
            val mandatory = etMandatory.text.toString().toIntOrNull() ?: 0
            val optional = etOptional.text.toString().toIntOrNull() ?: 0
            val savings = etSavings.text.toString().toIntOrNull() ?: 0

            if (mandatory + optional + savings > totalBalance) {
                Toast.makeText(this, "Сумма превышает баланс!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем план в SharedPreferences
            prefs.edit().apply {
                putInt("plan_mandatory", mandatory)
                putInt("plan_optional", optional)
                putInt("plan_savings", savings)
                putBoolean("is_perfect_plan", (mandatory + optional + savings) == totalBalance)
                apply()
            }

            Toast.makeText(this, "План бюджета сохранен!", Toast.LENGTH_SHORT).show()
            finish() // Возвращаемся на главный экран
        }
    }
}
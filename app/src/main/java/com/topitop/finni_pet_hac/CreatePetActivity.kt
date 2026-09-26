package com.topitop.finni_pet_hac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreatePetActivity : AppCompatActivity() {

    private var selectedSpecies = "fox" // owl, bear, fox
    private var selectedColor = "mint"  // mint, blue, orange

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pet)

        val etPetName = findViewById<EditText>(R.id.et_pet_name)
        val btnSave = findViewById<Button>(R.id.btn_save_pet)
        val ivPetPreview = findViewById<ImageView>(R.id.iv_pet_preview)

        // Кнопки вида
        val btnOwl = findViewById<Button>(R.id.btn_owl)
        val btnBear = findViewById<Button>(R.id.btn_bear)
        val btnFox = findViewById<Button>(R.id.btn_fox)

        // Кнопки цвета
        val btnMint = findViewById<Button>(R.id.btn_mint)
        val btnBlue = findViewById<Button>(R.id.btn_blue)
        val btnOrange = findViewById<Button>(R.id.btn_orange)

        // Функция обновления картинки
        val updatePreview = {
            val imageName = "pet_${selectedSpecies}_${selectedColor}"
            val resId = resources.getIdentifier(imageName, "drawable", packageName)
            if (resId != 0) {
                ivPetPreview.setImageResource(resId)
            }
        }

        // Функция подсветки выбранной кнопки
        val highlightBtn = { selected: Button, others: List<Button> ->
            selected.background = getDrawable(R.drawable.bg_block_blue)
            selected.setTextColor(0xFFFFFFFF.toInt())
            others.forEach {
                it.background = getDrawable(R.drawable.bg_card)
                it.setTextColor(0xFF7F8C8D.toInt())
            }
        }

        // Обработчики Вида
        btnOwl.setOnClickListener {
            selectedSpecies = "owl"
            highlightBtn(btnOwl, listOf(btnBear, btnFox))
            updatePreview()
        }
        btnBear.setOnClickListener {
            selectedSpecies = "bear"
            highlightBtn(btnBear, listOf(btnOwl, btnFox))
            updatePreview()
        }
        btnFox.setOnClickListener {
            selectedSpecies = "fox"
            highlightBtn(btnFox, listOf(btnOwl, btnBear))
            updatePreview()
        }

        // Обработчики Цвета
        btnMint.setOnClickListener {
            selectedColor = "mint"
            highlightBtn(btnMint, listOf(btnBlue, btnOrange))
            updatePreview()
        }
        btnBlue.setOnClickListener {
            selectedColor = "blue"
            highlightBtn(btnBlue, listOf(btnMint, btnOrange))
            updatePreview()
        }
        btnOrange.setOnClickListener {
            selectedColor = "orange"
            highlightBtn(btnOrange, listOf(btnMint, btnBlue))
            updatePreview()
        }

        // Активация кнопки сохранения при вводе имени
        etPetName.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val hasText = !s.isNullOrEmpty()
                btnSave.isEnabled = hasText
                btnSave.alpha = if (hasText) 1f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Сохранение и переход
        btnSave.setOnClickListener {
            val petName = etPetName.text.toString()
            val petImage = "pet_${selectedSpecies}_${selectedColor}"

            val prefs = getSharedPreferences("pet_profile", MODE_PRIVATE)
            prefs.edit().apply {
                putString("pet_name", petName)
                putString("pet_image", petImage)
                putInt("balance", 2156)
                putInt("savings", 2740)
                putInt("pet_mood", 70)
                putInt("pet_health", 80)
                putInt("pet_hunger", 60)
                putInt("pet_energy", 65)
                putInt("current_period", 1)
                putBoolean("is_first_launch", false)
                apply()
            }

            Toast.makeText(this, "Питомец $petName создан!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Устанавливаем значения по умолчанию при запуске
        highlightBtn(btnFox, listOf(btnOwl, btnBear))
        highlightBtn(btnMint, listOf(btnBlue, btnOrange))
        updatePreview()
    }
}
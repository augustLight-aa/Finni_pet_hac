package com.topitop.finni_pet_hac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnStart = findViewById<Button>(R.id.btn_start)

        btnStart.setOnClickListener {
            // Переход к созданию питомца
            startActivity(Intent(this, CreatePetActivity::class.java))
            finish() // Закрываем Welcome, чтобы нельзя было вернуться назад
        }
    }
}
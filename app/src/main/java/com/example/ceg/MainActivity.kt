package com.example.ceg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.Context
import android.content.SharedPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathTrainingApp(this)
        }
    }
}


class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    // Метод для сохранения числа
    fun saveMaxScore(number: Int) {
        if (readMaxScore() < number) {
            sharedPreferences.edit().putInt("max_score", number).apply()
        }
    }

    // Метод для чтения числа
    fun readMaxScore(): Int {
        return sharedPreferences.getInt("max_score", 0) // Возвращает 0, если значение не найдено
    }
}

enum class States {
    Lose,
    Incorrect,
    Question,
    Start
}
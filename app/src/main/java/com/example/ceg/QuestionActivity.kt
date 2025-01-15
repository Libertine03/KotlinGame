package com.example.ceg

import kotlin.random.Random
import kotlin.random.nextInt

data class Question(val text: String, val answer: Any, val isBoolean: Boolean = false) {
    fun checkAnswer(userAnswer: Any?): Boolean {
        return userAnswer == answer
    }

    fun correctAnswer() =
        "Правильный ответ: ${if (answer == true) "Да" else if (answer == false) "Нет" else answer}"
}

fun generateQuestion(difficulty: Int): Question {
    val random = Random.nextInt(0, 4) // Изменяем диапазон на 0-3

    // Увеличиваем диапазон случайных чисел в зависимости от уровня сложности
    val range = 1..(10 * difficulty)

    when (random) {
        0, 1, 2 -> {
            // Генерация простого арифметического примера
            val a = Random.nextInt(range)
            val b = Random.nextInt(range)
            val operation = Random.nextInt(0, 4) // 0-3 для 4 операций
            val answer: Int
            val operationSymbol: String

            when (operation) {
                0 -> {
                    answer = a + b
                    operationSymbol = "+"
                }

                1 -> {
                    answer = a - b
                    operationSymbol = "-"
                }

                2 -> {
                    answer = a * b
                    operationSymbol = "*"
                }

                else -> {
                    // Генерация деления с целым результатом
                    val divisor = Random.nextInt(1, range.last + 1) // делитель не может быть 0
                    val multiplier = Random.nextInt(1, (range.last / divisor) + 1) // множитель для получения делимого
                    val dividend = divisor * multiplier // делимое, которое делится на делитель без остатка
                    answer = dividend / divisor
                    operationSymbol = "/"
                    return Question("$dividend $operationSymbol $divisor = ?", answer)
                }
            }

            return Question("$a $operationSymbol $b = ?", answer)
        }

        3 -> {
            // Генерация логического выражения
            val a = Random.nextInt(range)
            val b = Random.nextInt(range)
            val operation = if (Random.nextBoolean()) "<" else ">"
            val answer = (operation == "<" && a < b) || (operation == ">" && a > b)
            return Question("$a $operation $b?", answer, isBoolean = true)
        }

        else -> {
            return Question("Дополнительный тип вопроса", false)
        }
    }
}
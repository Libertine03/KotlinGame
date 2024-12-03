package com.example.ceg

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ceg.ui.theme.CEGTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathTrainingApp()
        }
    }
}

@Composable
fun MathTrainingApp() {
    var score by remember { mutableIntStateOf(0) }
    var question by remember { mutableStateOf(generateQuestion()) }
    var userAnswer by remember { mutableStateOf("") }
    var incorrectAttempts by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf(States.Question) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8056AEC1))
    ) {
        when (state) {
            States.Lose -> {
                GameOverScreen(score) {
                    score = 0
                    incorrectAttempts = 0
                    question = generateQuestion()
                    state = States.Question
                }
            }

            States.Question -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Уровень: $score", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .height(Dp(150F))
                            .width(Dp(300F))
                            .background(Color(254, 174, 0), shape = RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = question.text,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (question.isBoolean) {
                        Spacer(modifier = Modifier.height(48.dp))
                        Row {
                            Button(
                                onClick = {
                                    if (question.checkAnswer(true)) {
                                        score++
                                        question = generateQuestion()
                                        state = States.Question
                                    } else {
                                        incorrectAttempts++
                                        state = if (incorrectAttempts >= 3) {
                                            States.Lose
                                        } else {
                                            States.Incorrect
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(Dp(140F))
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    "Да", color = Color.Black,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 28.sp,
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (question.checkAnswer(false)) {
                                        score++
                                        question = generateQuestion()
                                        state = States.Question
                                    } else {
                                        incorrectAttempts++
                                        state = if (incorrectAttempts >= 3) {
                                            States.Lose
                                        } else {
                                            States.Incorrect
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(Dp(140F))
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            )
                            {
                                Text(
                                    "Нет", color = Color.Black,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 28.sp
                                    )
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(48.dp))
                        TextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = { Text("Введите ответ") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val answer = userAnswer.toIntOrNull()
                                if (question.checkAnswer(answer)) {
                                    score++
                                    question = generateQuestion()
                                    userAnswer = ""
                                    state = States.Question
                                } else {
                                    userAnswer = ""
                                    incorrectAttempts++
                                    state = if (incorrectAttempts >= 3) {
                                        States.Lose
                                    } else {
                                        States.Incorrect
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(Dp(280F))
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                "Проверить", color = Color.Black,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 28.sp
                                )
                            )
                        }
                    }
                }
            }

            else -> {
                TrueAnswerScreen(question.correctAnswer()) {
                    state = States.Question
                    question = generateQuestion()
                }
            }
        }
    }
}


data class Question(val text: String, val answer: Any, val isBoolean: Boolean = false) {
    fun checkAnswer(userAnswer: Any?): Boolean {
        return userAnswer == answer
    }

    fun correctAnswer() =
        "Правильный ответ: ${if (answer == true) "Верно" else if (answer == false) "Неверно" else answer}"

}

fun generateQuestion(): Question {
    val random = Random.nextInt(0, 2) // Изменено на 3, чтобы включить новые операции

    when (random) {
        0 -> {
            // Генерация простого арифметического примера
            val a = Random.nextInt(1, 10)
            val b = Random.nextInt(1, 10)
            val operation = Random.nextInt(0, 4) // 0-3 для 4 операций
            val answer: Int
            val operationSymbol: String

            if (operation == 0) {
                answer = a + b
                operationSymbol = "+"
            } else if (operation == 1) {
                answer = a - b
                operationSymbol = "-"
            } else if (operation == 2) {
                answer = a * b
                operationSymbol = "*"
            } else {
                answer = if (b != 0) a / b else a // Избегаем деления на ноль
                operationSymbol = "/"
            }

            return Question("$a $operationSymbol $b = ?", answer)
        }
        1 -> {
            // Генерация логического выражения
            val a = Random.nextInt(1, 10)
            val b = Random.nextInt(1, 10)
            val operation = if (Random.nextBoolean()) "<" else ">"
            val answer = (operation == "<" && a < b) || (operation == ">" && a > b)
            return Question("$a $operation $b?", answer, isBoolean = true)
        }
        else -> {
            // Можно добавить дополнительные типы вопросов, если нужно
            return Question("Дополнительный тип вопроса", false) // Пример
        }
    }
}


@Composable
fun TrueAnswerScreen(answer: String, onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Неверно!", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 35.sp
            )
        )
        Text(text = answer, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(Dp(280F))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        ) {
            Text("Продолжить", color = Color.Black)
        }
    }
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Игра окончена!", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Уровень: $score", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(Dp(280F))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        ) {
            Text("Начать заново", color = Color.Black)
        }
    }
}

enum class States {
    Lose,
    Incorrect,
    Question
}
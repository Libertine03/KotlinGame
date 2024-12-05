package com.example.ceg

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MathTrainingApp(activity: ComponentActivity) {
    val sharedPreferencesManager = SharedPreferencesManager(activity)

    var score by remember { mutableIntStateOf(1) }
    var question by remember { mutableStateOf(generateQuestion(1)) }
    var userAnswer by remember { mutableStateOf("") }
    var incorrectAttempts by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf(States.Start) }
    val timerDuration = remember { mutableIntStateOf(0) }

    val isTimerRunning = remember { mutableStateOf(false) }
    val animatedValue = remember { mutableStateOf(Animatable(0f)) }
    val coroutineScope = rememberCoroutineScope()

    fun reloadTimer() {
        timerDuration.intValue = 15 + score * 2
        state = States.Question
        isTimerRunning.value = true
        coroutineScope.launch {
            animatedValue.value.snapTo(1f)
        }
    }

    fun stopTimer() {
        coroutineScope.launch {
            isTimerRunning.value = false
            animatedValue.value.snapTo(0f)
            timerDuration.intValue = 0
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dp(30F)),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularTimer(
            totalTime = timerDuration,
            onTimerFinish = {
                isTimerRunning.value = false
                incorrectAttempts++
                state = if (incorrectAttempts >= 3) {
                    States.Lose
                } else {
                    States.Incorrect
                }
            },
            isTimerRunning = isTimerRunning,
            animatedValue = animatedValue
        )
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8056AEC1))
    ) {
        when (state) {
            States.Lose -> {
                stopTimer()
                sharedPreferencesManager.saveMaxScore(score)
                GameOverScreen(score,
                    onRestart = {
                        score = 1
                        incorrectAttempts = 0
                        question = generateQuestion(score)
                        reloadTimer()
                    },
                    onMainMenu = {
                        state = States.Start
                    })
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
                            .width(Dp(350F))
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
                                        question = generateQuestion(score)
                                        reloadTimer()
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
                                        question = generateQuestion(score)
                                        reloadTimer()

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
                                    question = generateQuestion(score)
                                    userAnswer = ""
                                    reloadTimer()
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

            States.Start -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "Рекорд пройденных уровней: ${sharedPreferencesManager.readMaxScore()}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = Dp(55F))
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = {
                            state = States.Question
                            question = generateQuestion(score)
                            reloadTimer()
                        },
                        colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(horizontal = Dp(55F))
                            .width(Dp(295F))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    ) {
                        Text("Начать игру", color = Color.Black, fontSize = 26.sp)
                    }
                }
            }

            else -> {
                stopTimer()
                TrueAnswerScreen(question.correctAnswer()) {
                    state = States.Question
                    question = generateQuestion(score)
                    reloadTimer()
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
        "Правильный ответ: ${if (answer == true) "Да" else if (answer == false) "Нет" else answer}"
}

fun generateQuestion(difficulty: Int): Question {
    val random = Random.nextInt(0, 2)

    // Увеличиваем диапазон случайных чисел в зависимости от уровня сложности
    val range = 1..(10 * difficulty)

    when (random) {
        0 -> {
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
                    answer = if (b != 0) a / b else a // Избегаем деления на ноль
                    operationSymbol = "/"
                }
            }

            return Question("$a $operationSymbol $b = ?", answer)
        }

        1 -> {
            // Генерация логического выражения
            val a = Random.nextInt(range)
            val b = Random.nextInt(range)
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
            Text("Продолжить", color = Color.Black, fontSize = 20.sp)
        }
    }
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit, onMainMenu: () -> Unit) {
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
            Text("Начать заново", color = Color.Black, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onMainMenu,
            colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(Dp(280F))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        ) {
            Text("На главную страницу", color = Color.Black, fontSize = 20.sp)
        }
    }
}

@Composable
fun CircularTimer(
    modifier: Modifier = Modifier,
    totalTime: MutableIntState, // Общее время в секундах
    onTimerFinish: () -> Unit,
    isTimerRunning: MutableState<Boolean>,
    animatedValue: MutableState<Animatable<Float, AnimationVector1D>>
) {

    LaunchedEffect(totalTime.intValue) {
        if (isTimerRunning.value && totalTime.intValue > 0) {
            animatedValue.value.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = totalTime.intValue * 1000,
                    easing = LinearEasing
                )
            )
        }
    }
    LaunchedEffect(totalTime.intValue) {
        if (isTimerRunning.value) {

            if (totalTime.intValue > 0) {
                delay(1000L)
                totalTime.intValue--
            } else {
                onTimerFinish()
            }
        }
    }

    val textSize = 48.sp
    Box(modifier = modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                color = Color.LightGray,
                radius = size.minDimension / 2,
                style = Stroke(width = 20.dp.toPx())
            )
            drawArc(
                color = Color(254, 174, 0),
                startAngle = -90f,
                sweepAngle = animatedValue.value.value * 360f,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx()),
                size = size.copy(width = size.width, height = size.height)
            )
        }
        Text(
            text = totalTime.intValue.toString(),
            fontSize = textSize,
            color = Color.Black
        )
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
        return sharedPreferences.getInt("max_score", 33) // Возвращает 0, если значение не найдено
    }
}

enum class States {
    Lose,
    Incorrect,
    Question,
    Start
}
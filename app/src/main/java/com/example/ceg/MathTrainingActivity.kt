package com.example.ceg

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.times
import kotlinx.coroutines.delay
import kotlin.math.abs

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MathTrainingApp(activity: ComponentActivity) {
    val isKeyboardOpen by keyboardAsState() // true or false
    val animatedFloat by animateFloatAsState(
        targetValue = if (isKeyboardOpen) 100f else 200f,
        label = "",
    )

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

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val systemPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(Color(0x8056AEC1))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CircularTimer(
                modifier = Modifier
                    .padding(top = systemPadding + 25.dp),
                timerSize = animatedFloat,
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
                        Text(
                            text = "Уровень: $score",
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize =  animatedFloat / 200 * 35.sp
                        )
                        Text(
                            text = "Осталось жизней: ${abs(incorrectAttempts - 3)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize =  animatedFloat / 200 * 35.sp,
                            color = when (abs(incorrectAttempts - 3)) {
                                3 -> Color(0xFF006400)
                                2 -> Color(0xFFCC8400)
                                1 -> Color.Red
                                else -> Color.Gray
                            }
                        )
                        //Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .height(Dp(150F * animatedFloat / 200))
                                .width(Dp(350F * animatedFloat / 200))
                                .background(Color(254, 174, 0), shape = RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = question.text,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize =  animatedFloat / 200 * 52.sp,
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
                                    border = BorderStroke(2.dp, Color.Black),
                                    modifier = Modifier
                                        .width(Dp(140F))
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
                                    border = BorderStroke(2.dp, Color.Black),
                                    modifier = Modifier
                                        .width(Dp(140F))
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
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { state ->
                                    },
                                value = userAnswer,
                                onValueChange = { userAnswer = it },
                                label = { Text("Введите ответ") },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
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
                                border = BorderStroke(2.dp, Color.Black),
                                modifier = Modifier
                                    .width(Dp(280F))
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
                            fontSize = 19.sp,
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
                            border = BorderStroke(2.dp, Color.Black),
                            modifier = Modifier
                                .padding(horizontal = Dp(55F))
                                .width(Dp(295F))
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
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun CircularTimer(
    modifier: Modifier = Modifier,
    timerSize: Float,
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
    Box(modifier = modifier.size(timerSize.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                color = Color.LightGray,
                radius = size.minDimension / 2,
                style = Stroke(width = timerSize / 200 * 20.dp.toPx())
            )
            drawArc(
                color = Color(254, 174, 0),
                startAngle = -90f,
                sweepAngle = animatedValue.value.value * 360f,
                useCenter = false,
                style = Stroke(width = timerSize / 200 * 20.dp.toPx()),
                size = size.copy(width = size.width, height = size.height)
            )
        }
        Text(
            text = totalTime.intValue.toString(),
            fontSize = textSize * timerSize / 200,
            color = Color.Black
        )
    }
}
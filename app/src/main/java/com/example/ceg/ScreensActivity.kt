package com.example.ceg

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier
                .width(Dp(280F))
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
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier
                .width(Dp(280F))
        ) {
            Text("Начать заново", color = Color.Black, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onMainMenu,
            colors = ButtonDefaults.buttonColors(Color(254, 174, 0)),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier
                .width(Dp(280F))
        ) {
            Text("На главную страницу", color = Color.Black, fontSize = 20.sp)
        }
    }
}
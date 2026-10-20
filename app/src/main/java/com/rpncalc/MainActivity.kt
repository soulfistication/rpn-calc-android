package com.rpncalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val DisplayGreen = Color(0xFF4CAF50)
private val PanelBg = Color(0xFF1F1F1F)
private val DigitKeyBg = Color(0xFF333333)
private val KeyOrange = Color(0xFFFF9800)
private val FuncYellow = Color(0xFFFFEB3B)
private val EnterGreen = Color(0xFF4CAF50)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val vm: RpnCalculatorViewModel = viewModel()
                    CalculatorScreen(vm)
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(calc: RpnCalculatorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RPN-90",
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(top = 24.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(PanelBg, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StackRow("T", calc.formatNumber(calc.t))
            StackRow("Z", calc.formatNumber(calc.z))
            StackRow("Y", calc.formatNumber(calc.y))
            StackRow("X", calc.displayX)
        }

        calc.errorMessage?.let { err ->
            Text(
                text = err,
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f, fill = true))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FunctionKey("±", Modifier.weight(1f), height = 52.dp) { calc.pressChangeSign() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FunctionKey("CLX", Modifier.weight(1f)) { calc.clearX() }
                FunctionKey("DROP", Modifier.weight(1f)) { calc.dropX() }
                FunctionKey("SWAP", Modifier.weight(1f)) { calc.swapXY() }
                FunctionKey("CLR", Modifier.weight(1f)) { calc.clearAll() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OpKey("÷", Modifier.weight(1f)) { calc.divide() }
                OpKey("×", Modifier.weight(1f)) { calc.multiply() }
                OpKey("−", Modifier.weight(1f)) { calc.subtract() }
                OpKey("+", Modifier.weight(1f)) { calc.add() }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitKey("7", Modifier.weight(1f)) { calc.pressDigit("7") }
                DigitKey("8", Modifier.weight(1f)) { calc.pressDigit("8") }
                DigitKey("9", Modifier.weight(1f)) { calc.pressDigit("9") }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitKey("4", Modifier.weight(1f)) { calc.pressDigit("4") }
                DigitKey("5", Modifier.weight(1f)) { calc.pressDigit("5") }
                DigitKey("6", Modifier.weight(1f)) { calc.pressDigit("6") }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitKey("1", Modifier.weight(1f)) { calc.pressDigit("1") }
                DigitKey("2", Modifier.weight(1f)) { calc.pressDigit("2") }
                DigitKey("3", Modifier.weight(1f)) { calc.pressDigit("3") }
                Spacer(Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DigitKey("0", Modifier.weight(2f)) { calc.pressDigit("0") }
                DigitKey(".", Modifier.weight(1f)) { calc.pressDecimal() }
            }
            Row(Modifier.fillMaxWidth()) {
                EnterKey(Modifier.fillMaxWidth()) { calc.pressEnter() }
            }
        }
    }
}

@Composable
private fun StackRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            color = DisplayGreen,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DigitKey(title: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DigitKeyBg,
            contentColor = Color.White
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OpKey(title: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KeyOrange,
            contentColor = Color.White
        )
    ) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FunctionKey(
    title: String,
    modifier: Modifier,
    height: androidx.compose.ui.unit.Dp = 40.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = FuncYellow
        ),
        border = BorderStroke(1.dp, FuncYellow.copy(alpha = 0.7f))
    ) {
        Text(text = title, fontSize = if (height == 40.dp) 18.sp else 24.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EnterKey(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EnterGreen,
            contentColor = Color.Black
        )
    ) {
        Text(text = "ENTER", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

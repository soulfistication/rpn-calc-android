package com.rpncalc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class RpnCalculatorViewModel : ViewModel() {

    var stack by mutableStateOf(listOf(0.0, 0.0, 0.0, 0.0))
        private set
    var inputBuffer by mutableStateOf("")
        private set
    var showingInput by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val x: Double get() = stack[0]
    val y: Double get() = stack[1]
    val z: Double get() = stack[2]
    val t: Double get() = stack[3]

    val displayX: String
        get() = if (showingInput && inputBuffer.isNotEmpty()) inputBuffer else formatNumber(x)

    fun pressDigit(digit: String) {
        if (digit.length != 1 || digit !in "0123456789") return
        if (!showingInput) {
            inputBuffer = ""
            showingInput = true
        }
        inputBuffer = if (inputBuffer == "0") digit else inputBuffer + digit
    }

    fun pressDecimal() {
        if (!showingInput) {
            inputBuffer = "0"
            showingInput = true
        }
        if (!inputBuffer.contains(".")) {
            inputBuffer = if (inputBuffer.isEmpty()) "0." else inputBuffer + "."
        }
    }

    fun pressChangeSign() {
        if (showingInput && inputBuffer.isNotEmpty()) {
            inputBuffer = if (inputBuffer.startsWith("-")) {
                inputBuffer.removePrefix("-")
            } else {
                "-$inputBuffer"
            }
        } else {
            setX(-x)
        }
    }

    fun pressEnter() {
        commitInputToX()
        val s = stack.toMutableList()
        val xv = s[0]
        s.add(0, xv)
        while (s.size > 4) s.removeAt(s.lastIndex)
        stack = s.toList()
        showingInput = false
    }

    private fun binaryOp(op: (Double, Double) -> Double?) {
        commitInputToX()
        val xVal = stack[0]
        val yVal = stack[1]
        val result = op(yVal, xVal) ?: run {
            errorMessage = "Math Error"
            return
        }
        val s = stack.toMutableList()
        repeat(2) { s.removeAt(0) }
        s.add(0, result)
        while (s.size < 4) s.add(0.0)
        stack = s.take(4)
        showingInput = false
    }

    fun add() = binaryOp { a, b -> a + b }

    fun subtract() = binaryOp { a, b -> a - b }

    fun multiply() = binaryOp { a, b -> a * b }

    fun divide() = binaryOp { yReg, xReg ->
        if (xReg == 0.0) null else yReg / xReg
    }

    fun swapXY() {
        if (showingInput) commitInputToX()
        val s = stack.toMutableList()
        val tmp = s[0]
        s[0] = s[1]
        s[1] = tmp
        stack = s.toList()
    }

    fun dropX() {
        if (showingInput) {
            inputBuffer = ""
            showingInput = false
            return
        }
        val s = stack.toMutableList()
        val tVal = s[3]
        s.removeAt(0)
        s.add(tVal)
        stack = s.toList()
    }

    fun clearX() {
        inputBuffer = ""
        showingInput = true
    }

    fun clearAll() {
        stack = listOf(0.0, 0.0, 0.0, 0.0)
        inputBuffer = ""
        showingInput = false
        errorMessage = null
    }

    private fun commitInputToX() {
        if (!showingInput || inputBuffer.isEmpty()) return
        inputBuffer.toDoubleOrNull()?.let { setX(it) }
        inputBuffer = ""
        showingInput = false
    }

    private fun setX(value: Double) {
        val s = stack.toMutableList()
        s[0] = value
        stack = s.toList()
    }

    fun formatNumber(value: Double): String {
        if (value == 0.0) return "0"
        val absVal = kotlin.math.abs(value)
        val symbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
        return if (absVal >= 1e8 || absVal < 1e-4) {
            DecimalFormat("0.######E0", symbols).format(value)
        } else {
            DecimalFormat().apply {
                decimalFormatSymbols = symbols
                isGroupingUsed = false
                maximumFractionDigits = 8
                minimumFractionDigits = 0
            }.format(value)
        }
    }
}

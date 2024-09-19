package com.example.shengwenouyang_calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.shengwenouyang_calculator.ui.theme.ShengwenOuyangCalculatorTheme
import java.lang.ArithmeticException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShengwenOuyangCalculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Display Input and Result
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter expression") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Text(
                    text = if (errorMessage.isNotEmpty()) errorMessage else result,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                // Calculator buttons
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        CalculatorButton("1") { input += "1" }
                        CalculatorButton("2") { input += "2" }
                        CalculatorButton("3") { input += "3" }
                        CalculatorButton("+") { input += "+" }
                    }
                    Row {
                        CalculatorButton("4") { input += "4" }
                        CalculatorButton("5") { input += "5" }
                        CalculatorButton("6") { input += "6" }
                        CalculatorButton("-") { input += "-" }
                    }
                    Row {
                        CalculatorButton("7") { input += "7" }
                        CalculatorButton("8") { input += "8" }
                        CalculatorButton("9") { input += "9" }
                        CalculatorButton("*") { input += "*" }
                    }
                    Row {
                        CalculatorButton("0") { input += "0" }
                        CalculatorButton("C") { input = ""; result = ""; errorMessage = "" }
                        CalculatorButton("=") {
                            try {
                                result = evaluateExpression(input)
                                errorMessage = ""
                            } catch (e: ArithmeticException) {
                                errorMessage = "Error: ${e.message}"
                            } catch (e: Exception) {
                                errorMessage = "Invalid expression"
                            }
                        }
                        CalculatorButton("/") { input += "/" }
                    }
                }
            }
        }
    )
}

@Composable
fun CalculatorButton(symbol: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(8.dp).size(64.dp)) {
        Text(symbol)
    }
}


fun evaluateExpression(expression: String): String {
    return try {
        val tokens = parseTokens(expression)
        val result = calculate(tokens)
        result.toString()
    } catch (e: ArithmeticException) {
        throw ArithmeticException("Cannot divide by zero")
    } catch (e: Exception) {
        throw Exception("Invalid expression")
    }
}

fun parseTokens(expression: String): MutableList<String> {
    val tokens = mutableListOf<String>()
    var currentNumber = StringBuilder()

    for (char in expression) {
        when (char) {
            in '0'..'9', '.' -> {
                currentNumber.append(char)
            }
            '+', '-', '*', '/' -> {
                if (currentNumber.isNotEmpty()) {
                    tokens.add(currentNumber.toString())
                    currentNumber.clear()
                }
                tokens.add(char.toString())
            }
            else -> throw Exception("Invalid character in expression")
        }
    }
    if (currentNumber.isNotEmpty()) {
        tokens.add(currentNumber.toString())
    }

    return tokens
}


fun calculate(tokens: MutableList<String>): Double {
    val operatorPrecedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)


    var index = 0
    while (index < tokens.size) {
        if (tokens[index] == "*" || tokens[index] == "/") {
            val left = tokens[index - 1].toDouble()
            val right = tokens[index + 1].toDouble()
            val result = if (tokens[index] == "*") left * right else {
                if (right == 0.0) throw ArithmeticException("Cannot divide by zero")
                left / right
            }
            tokens[index - 1] = result.toString()
            tokens.removeAt(index)
            tokens.removeAt(index)
        } else {
            index++
        }
    }


    index = 0
    while (index < tokens.size) {
        if (tokens[index] == "+" || tokens[index] == "-") {
            val left = tokens[index - 1].toDouble()
            val right = tokens[index + 1].toDouble()
            val result = if (tokens[index] == "+") left + right else left - right
            tokens[index - 1] = result.toString()
            tokens.removeAt(index)
            tokens.removeAt(index)
        } else {
            index++
        }
    }

    return tokens[0].toDouble()
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    ShengwenOuyangCalculatorTheme {
        CalculatorApp()
    }
}

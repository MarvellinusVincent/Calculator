package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvCalculator: TextView // Displays the calculator input/output
    private var currentNumber: StringBuilder = StringBuilder() // The current input number
    private var firstOperand: Double? = null // The first operand for the current operation
    private var operator: String? = null // The operator for the current operation
    private var lastOperation: String? = null // The last performed operation
    private var lastSecondOperand: Double? = null // The second operand for the last performed operation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCalculator = findViewById(R.id.tvCalculator)

        setupNumberButtons()
        setupOperationButtons()
        setupEqualButton()
        setupClearButton()
        setupDecimalButton()
        setupPercentageButton()
        setupChangePosNegButton()
    }

    // Sets up click listeners for the number buttons
    private fun setupNumberButtons() {
        val numberButtonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9
        )

        for (id in numberButtonIds) {
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                if (operator == null) {
                    if (currentNumber.toString() == "0") {
                        currentNumber.clear() // Clears the StringBuilder
                    }
                }
                currentNumber.append(button.text)
                updateTopTextView()
            }
        }
    }

    // Sets up slick listeners for the operation buttons
    private fun setupOperationButtons() {
        val operationButtonIds = arrayOf(
            R.id.buttonAdd, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide)

        for (id in operationButtonIds) {
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                if (operator != null && firstOperand != null) {
                    val secondOperand = currentNumber.toString().toDoubleOrNull()
                    if (secondOperand != null) {
                        val result = performOperation(firstOperand!!, secondOperand, operator!!)
                        currentNumber.clear() // Clears the StringBuilder
                        currentNumber.append(result)
                        updateTopTextView()
                        firstOperand = result
                    }
                }
                operator = button.text.toString()
                firstOperand = currentNumber.toString().toDoubleOrNull()
                currentNumber.clear() // Clears the StringBuilder
            }
        }
    }

    // Sets up slick listeners for the equal button
    private fun setupEqualButton() {
        val equalButton = findViewById<Button>(R.id.buttonEquals)
        equalButton.setOnClickListener {
            if (firstOperand != null && operator != null) {
                val secondOperand = currentNumber.toString().toDoubleOrNull()
                if (secondOperand != null) {
                    val result = performOperation(firstOperand!!, secondOperand, operator!!)
                    lastOperation = operator
                    lastSecondOperand = secondOperand
                    currentNumber.clear() // Clears the StringBuilder
                    currentNumber.append(result)
                    updateTopTextView()
                    firstOperand = result
                    operator = null
                }
            } else if (lastOperation != null && lastSecondOperand != null) {
                val result = performOperation(firstOperand!!, lastSecondOperand!!, lastOperation!!)
                currentNumber.clear() // Clears the StringBuilder
                currentNumber.append(result)
                updateTopTextView()
                firstOperand = result
            }
        }
    }

    // Sets up slick listeners for the clear button
    private fun setupClearButton() {
        val clearButton = findViewById<Button>(R.id.buttonClear)
        clearButton.setOnClickListener {
            currentNumber.clear() // Clears the StringBuilder
            currentNumber.append("0")
            firstOperand = null
            operator = null
            updateTopTextView()
        }
    }

    // Sets up slick listeners for the decimal button
    private fun setupDecimalButton() {
        val decimalButton = findViewById<Button>(R.id.buttonDecimal)
        decimalButton.setOnClickListener {
            if (!currentNumber.contains(".")) {
                currentNumber.append(".")
                updateTopTextView()
            }
        }
    }

    // Sets up slick listeners for the percentage button
    private fun setupPercentageButton() {
        val percentageButton = findViewById<Button>(R.id.buttonPercentage)
        percentageButton.setOnClickListener {
            val value = currentNumber.toString().toDoubleOrNull()
            if (value != null) {
                val percentageValue = value / 100
                currentNumber.clear() // Clears the StringBuilder
                currentNumber.append(percentageValue)
                updateTopTextView()
            }
        }
    }

    // Sets up slick listeners for the +/- button
    private fun setupChangePosNegButton() {
        val changePosNeg = findViewById<Button>(R.id.buttonChangePosNeg)
        changePosNeg.setOnClickListener {
            if (currentNumber.isNotEmpty()) {
                val value = currentNumber.toString().toDouble()
                currentNumber.clear() // Clears the StringBuilder
                currentNumber.append((-value).toString())
                updateTopTextView()
            }
        }
    }

    // The function for performing the operations
    private fun performOperation(first: Double, second: Double, operator: String): Double {
        return when (operator) {
            "+" -> first + second
            "-" -> first - second
            "X" -> first * second
            "/" -> first / second
            else -> 0.0
        }
    }

    // The function to display the output to the StringBuilder
    private fun updateTopTextView() {
        tvCalculator.text = currentNumber.toString()
    }
}

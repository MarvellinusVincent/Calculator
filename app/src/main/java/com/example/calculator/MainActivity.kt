package com.example.calculator

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.content.res.Configuration
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.tan

/**
 * MainActivity represents the main calculator activity
 */
class MainActivity : AppCompatActivity() {

    /**
     * tvCalculator = Displays the calculator input/output
     * currentNumber = The current input number
     * firstOperand = The first operand for the current operation
     * operator = The operator for the current operation
     * lastOperation = The last performed operation
     * lastSecondOperand = The second operand for the last performed operation
     */
    private lateinit var tvCalculator: TextView
    private var currentNumber: StringBuilder = StringBuilder()
    private var firstOperand: Double? = null
    private var operator: String? = null
    private var lastOperation: String? = null
    private var lastSecondOperand: Double? = null
    private val TAG = "CalculatorApp"

    /**
     * Called when the activity is first created
     */
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
        setUpTrig()
        setUpLogs()
    }

    /**
     * Called to save the current state of the activity
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentNumber", currentNumber.toString())
        outState.putString("operator", operator)
        outState.putString("lastOperation", lastOperation)
        outState.putDouble("firstOperand", firstOperand ?: 0.0)
        outState.putDouble("lastSecondOperand", lastSecondOperand ?: 0.0)
    }

    /**
     * Called when the activity is restoring its previously saved state
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNumber = StringBuilder(savedInstanceState.getString("currentNumber", "0"))
        operator = savedInstanceState.getString("operator")
        lastOperation = savedInstanceState.getString("lastOperation")
        firstOperand = savedInstanceState.getDouble("firstOperand", 0.0)
        lastSecondOperand = savedInstanceState.getDouble("lastSecondOperand", 0.0)
        updateTopTextView() // Update the TextView to display the restored input
    }

    /**
     * Sets up click listeners for the number buttons
     */
    private fun setupNumberButtons() {
        val numberButtonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9
        )

        for (id in numberButtonIds) {
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                Log.d(TAG, "Button ${button.text} pressed")
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

    /**
     * Sets up click listeners for the operation buttons
     */
    private fun setupOperationButtons() {
        val operationButtonIds = arrayOf(
            R.id.buttonAdd, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide)

        for (id in operationButtonIds) {
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                Log.d(TAG, "Button ${button.text} pressed")
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

    /**
     * Sets up click listeners for the equal button
     */
    private fun setupEqualButton() {
        val equalButton = findViewById<Button>(R.id.buttonEquals)
        equalButton.setOnClickListener {
            Log.d(TAG, "Button ${equalButton.text} pressed")
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

    /**
     * Sets up click listeners for the clear button
     */
    private fun setupClearButton() {
        val clearButton = findViewById<Button>(R.id.buttonClear)
        clearButton.setOnClickListener {
            Log.d(TAG, "Button ${clearButton.text} pressed")
            currentNumber.clear() // Clears the StringBuilder
            currentNumber.append("0")
            firstOperand = null
            operator = null
            updateTopTextView()
        }
    }

    /**
     * Sets up click listeners for the decimal button
     */
    private fun setupDecimalButton() {
        val decimalButton = findViewById<Button>(R.id.buttonDecimal)
        decimalButton.setOnClickListener {
            Log.d(TAG, "Button ${decimalButton.text} pressed")
            if (!currentNumber.contains(".")) {
                currentNumber.append(".")
                updateTopTextView()
            }
        }
    }

    /**
     * Sets up click listeners for the percentage button
     */
    private fun setupPercentageButton() {
        val percentageButton = findViewById<Button>(R.id.buttonPercentage)
        percentageButton.setOnClickListener {
            Log.d(TAG, "Button ${percentageButton.text} pressed")
            val value = currentNumber.toString().toDoubleOrNull()
            if (value != null) {
                val percentageValue = value / 100
                currentNumber.clear() // Clears the StringBuilder
                currentNumber.append(percentageValue)
                updateTopTextView()
            }
        }
    }

    /**
     * Sets up click listeners for the +/- button
     */
    private fun setupChangePosNegButton() {
        val changePosNeg = findViewById<Button>(R.id.buttonChangePosNeg)
        changePosNeg.setOnClickListener {
            Log.d(TAG, "Button ${changePosNeg.text} pressed")
            if (currentNumber.isNotEmpty()) {
                val value = currentNumber.toString().toDouble()
                currentNumber.clear() // Clears the StringBuilder
                currentNumber.append((-value).toString())
                updateTopTextView()
            }
        }
    }

    /**
     * Sets up click listeners for the trig buttons
     */
    private fun setUpTrig() {
        val trig = arrayOf(R.id.buttonSin, R.id.buttonCos, R.id.buttonTan)
        for (id in trig) {
            val button = findViewById<Button>(id)
            button?.setOnClickListener {
                if (button != null) {
                    Log.d(TAG, "Button ${button.text} pressed")
                    val value = currentNumber.toString().toDoubleOrNull()
                    if (value != null) {
                        val trigValue = when (id) {
                            R.id.buttonSin -> sin(Math.toRadians(value))
                            R.id.buttonCos -> cos(Math.toRadians(value))
                            R.id.buttonTan -> tan(Math.toRadians(value))
                            else -> 0.0
                        }
                        currentNumber.clear() // Clears the StringBuilder
                        currentNumber.append(trigValue)
                        updateTopTextView()
                    }
                }
            }
        }
    }

    /**
     * Sets up click listeners for the log buttons
     */
    private fun setUpLogs() {
        val logs = arrayOf(R.id.buttonLog, R.id.buttonln)
        for (id in logs) {
            val button = findViewById<Button>(id)
            button?.setOnClickListener {
                if (button != null) {
                    Log.d(TAG, "Button ${button.text} pressed")
                    val value = currentNumber.toString().toDoubleOrNull()
                    if (value != null) {
                        val logValue = when (id) {
                            R.id.buttonLog -> log10(value)
                            R.id.buttonln -> ln(value)
                            else -> 0.0
                        }
                        currentNumber.clear() // Clears the StringBuilder
                        currentNumber.append(logValue)
                        updateTopTextView()
                    }
                }
            }
        }
    }

    /**
     * The function to perform the basic arithmetic operations
     */
    private fun performOperation(first: Double, second: Double, operator: String): Double {
        return when (operator) {
            "+" -> first + second
            "-" -> first - second
            "X" -> first * second
            "/" -> first / second
            else -> 0.0
        }
    }

    /**
     * The function to display the output to the StringBuilder
     */
    private fun updateTopTextView() {
        tvCalculator.text = currentNumber.toString()
    }
}
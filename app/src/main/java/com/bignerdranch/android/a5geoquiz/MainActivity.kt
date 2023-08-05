package com.bignerdranch.android.a5geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bignerdranch.android.a5geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var snackbar: Snackbar

    private val quizViewModel: QuizViewModel by viewModels()
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            toast(R.string.judgment_toast)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        snackbar = Snackbar.make(binding.con, "", Snackbar.LENGTH_INDEFINITE)

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            nextQuestion()
        }

        binding.questionTextView.setOnClickListener {
            nextQuestion()
        }

        binding.previousButton.setOnClickListener {
            if (quizViewModel.getCurrentIndex() != 0) {
                quizViewModel.previousQuestion()
                updateQuestion()
                enabledButton()
            }
            snackbarDismiss()
        }

        binding.cheatButton.setOnClickListener {
            val answerTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(
                this@MainActivity,
                answerTrue
            )
            cheatLauncher.launch(intent)

            //startActivity(intent)
        }

        visiblePreviousButton()
        enabledButton()
        updateQuestion()
        blockedAllButton()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun enabledButton() {
        if (quizViewModel.contAnswerBank()) {
            binding.apply {
                trueButton.isEnabled = false
                falseButton.isEnabled = false
            }
        } else {
            binding.apply {
                trueButton.isEnabled = true
                falseButton.isEnabled = true
            }
        }
    }

    private fun snackbarDismiss() {
        if (snackbar.isShown) snackbar.dismiss()
    }

    private fun nextQuestion() {
        quizViewModel.moveToNext()
        updateQuestion()
        enabledButton()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        visiblePreviousButton()
        if (quizViewModel.isCheater) toast(R.string.judgment_toast)
    }

    private fun checkAnswer(userAnswer: Boolean) {

        quizViewModel.addAnswerBank()

        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
           // quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        toast(messageResId)

        if (messageResId == R.string.correct_toast) quizViewModel.setAnswerTrueIndex()

        blockedAllButton()
        enabledButton()
    }

    @SuppressLint("StringFormatMatches")
    fun blockedAllButton() {
        if (quizViewModel.equalsBank()) {
            binding.apply {
                nextButton.isEnabled = false
                previousButton.isEnabled = false
                questionTextView.isEnabled = false
            }

            val textSnackbar = getString(
                R.string.true_answer,
                "${quizViewModel.percentTrueAnswer()}",
                "%"
            )

            snackbar.setText(textSnackbar)
            snackbar.setAction("Сброс") {
                snackbarDismiss()
                quizViewModel.clearAnswer()
                nextQuestion()
                binding.apply {
                    questionTextView.isEnabled = true
                    nextButton.isEnabled = true
                    previousButton.isEnabled = true
                }
            }

            snackbar.setActionTextColor(Color.RED)
            val sView = snackbar.view
            val text: TextView = sView.findViewById(
                com.google.android.material.R.id.snackbar_text
            )
            text.setTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    private fun toast(messageResId: Int) {
        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 260)
        toast.show()
    }

    private fun visiblePreviousButton() {
        binding.previousButton.isVisible = quizViewModel.getCurrentIndex() != 0
    }
}
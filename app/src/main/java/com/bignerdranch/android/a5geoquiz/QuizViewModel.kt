package com.bignerdranch.android.a5geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
private const val ANSWER_TRUE_INDEX_KEY = "ANSWER_TRUE_INDEX_KEY"
private const val ANSWER_BANK_KEY = "ANSWER_BANK_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
const val IS_CHEATER_ANSWER_KEY = "IS_CHEATER_ANSWER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var currentIndex: Int
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    private var answerTrueIndex
        get() = savedStateHandle[ANSWER_TRUE_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(ANSWER_TRUE_INDEX_KEY, value)

    private val answerBank: MutableList<Int>
        get() = savedStateHandle[ANSWER_BANK_KEY] ?: mutableListOf()

    var isCheater: Boolean
        get() = savedStateHandle[IS_CHEATER_KEY] ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    var isAnswerShown: Boolean
        get() = savedStateHandle[IS_CHEATER_ANSWER_KEY] ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_ANSWER_KEY, value)


    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    @JvmName("getCurrentIndex1")
    fun getCurrentIndex() = currentIndex

    fun previousQuestion() {
        currentIndex -= 1
    }

    fun addAnswerBank() {
        savedStateHandle[ANSWER_BANK_KEY] = answerBank
        answerBank.add(currentIndex)
        savedStateHandle[ANSWER_BANK_KEY] = answerBank
    }

    fun contAnswerBank() = answerBank.contains(currentIndex)

    fun setAnswerTrueIndex() {
        answerTrueIndex += 1
    }

    fun equalsBank(): Boolean {
        return questionBank.size == answerBank.size
    }

    fun percentTrueAnswer() = ((answerTrueIndex.toDouble() / questionBank.size) * 100).toInt()

    fun clearAnswer() {
        answerTrueIndex = 0
        answerBank.clear()
        savedStateHandle[ANSWER_BANK_KEY] = null
    }
}
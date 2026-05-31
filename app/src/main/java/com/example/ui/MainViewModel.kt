package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.StudentProfile
import com.example.data.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

// Navigation Screens
enum class Screen {
    Dashboard,
    LessonAddition,
    LessonSubtraction,
    LessonMultiplication,
    LessonDivision,
    LessonMathSetup,
    LessonMissingNum,
    LessonSudoku,
    LessonWordMatch,
    LessonSpelling,
    LessonCubeCounting,
    MathMinigame,
    ToyShop,
    ProfileConfig
}

// Math Game State
data class MathQuestion(
    val val1: Int,
    val val2: Int,
    val op: String, // "+" or "-"
    val correctAnswer: Int,
    val options: List<Int>,
    val emojiCountVal1: String = "🍎",
    val emojiCountVal2: String = "🍏"
)

// Cube Counting Game State
data class CubeColumn(
    val x: Int,
    val y: Int,
    val height: Int
)

data class CubeStackQuestion(
    val difficulty: String, // "Easy", "Medium", "Hard"
    val columns: List<CubeColumn>,
    val correctAnswer: Int,
    val options: List<Int>,
    val hint: String
)

// Missing Number Game State
data class MissingNumberQuestion(
    val val1: Int?, // if null, form: ? + val2 = target
    val val2: Int?, // if null, form: val1 + ? = target
    val op: String, // "+" or "-"
    val displayString: String, // e.g. "4 + ❓ = 9"
    val correctAnswer: Int,
    val options: List<Int>
)

// Sudoku Game State
data class SudokuCell(
    val row: Int,
    val col: Int,
    val correctValue: Int,
    var currentValue: Int, // 0 for empty
    val isGiven: Boolean
)

data class SudokuBoard(
    val cells: List<SudokuCell>,
    val size: Int = 4 // 4x4 Mini-Sudoku is ideal for 7-8 years
)

// Word Matching Game State
data class WordMatchItem(
    val word: String,
    val emoji: String,
    var isMatched: Boolean = false
)

data class WordMatchingRound(
    val leftWords: List<String>, // Words to select
    val rightEmojis: List<String>, // Emojis to select
    val pairs: List<WordMatchItem> // Correct mapping
)

// Spelling Puzzle State
data class SpellingPuzzle(
    val targetWord: String,
    val targetEmoji: String,
    val jumbledLetters: List<String>,
    var currentAnswerList: List<String> = emptyList()
)

// Bubble Pop Minigame Bubble
data class BubbleItem(
    val id: Int,
    val questionHex: String,
    val formula: String,
    val value: Int,
    val isCorrect: Boolean,
    var isPopped: Boolean = false,
    val xOffsetFraction: Float, // horizontal position fraction
    var yPosition: Float // vertical position
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StudentRepository(database.studentProfileDao())
        
        // Setup initial default profile
        viewModelScope.launch {
            repository.getOrCreateProfile()
        }
    }

    // Reactive Profile state flow
    val profileState: StateFlow<StudentProfile?> = repository.activeProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Current Navigation Screen
    var currentScreen by mutableStateOf(Screen.Dashboard)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        // Regenerate respective games when entering pages
        when (screen) {
            Screen.LessonAddition -> generateMathQuest()
            Screen.LessonSubtraction -> generateMathQuest()
            Screen.LessonMultiplication -> generateMathQuest()
            Screen.LessonDivision -> generateMathQuest()
            Screen.LessonMissingNum -> generateMissingNumberLesson()
            Screen.LessonSudoku -> generateSudokuQuiz()
            Screen.LessonWordMatch -> generateWordMatchingLesson()
            Screen.LessonSpelling -> generateSpellingLesson()
            Screen.LessonCubeCounting -> generateCubeCountingLesson()
            Screen.MathMinigame -> startBubblePopMinigame()
            else -> {}
        }
    }

    // App constants for Avatars & Toys
    val shopAvatars = listOf(
        AvatarItem("🦁", 0, true),
        AvatarItem("🐨", 0, true),
        AvatarItem("🦊", 0, true),
        AvatarItem("🐯", 15, false),
        AvatarItem("🐼", 30, false),
        AvatarItem("🦄", 45, false),
        AvatarItem("🦖", 60, false),
        AvatarItem("🐉", 80, false),
        AvatarItem("👽", 100, false),
        AvatarItem("🚀", 120, false)
    )

    val shopToys = listOf(
        ToyItem("🧸", "Cute Teddy Bear", 8),
        ToyItem("🎈", "Water Balloon Set", 5),
        ToyItem("🚂", "Choo Choo Train", 20),
        ToyItem("🛹", "Gamer Skateboard", 25),
        ToyItem("🎨", "Art & Pixel Board", 35),
        ToyItem("🛸", "Spaceship Drone", 50),
        ToyItem("👑", "Lesser King Tiara", 65),
        ToyItem("🏰", "Royal LEGO Castle", 100)
    )

    data class AvatarItem(val emoji: String, val price: Int, var defaultUnlocked: Boolean)
    data class ToyItem(val emoji: String, val name: String, val price: Int)

    fun changeAvatar(emoji: String) {
        viewModelScope.launch {
            repository.updateAvatar(emoji)
        }
    }

    fun buyAvatar(emoji: String, cost: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val success = repository.purchaseAvatar(emoji, cost)
            if (success) {
                onSuccess()
            } else {
                val profile = repository.getOrCreateProfile()
                if (profile.coins < cost) {
                    onError("Not enough coins! Play more math to earn!")
                } else {
                    onError("Already unlocked!")
                }
            }
        }
    }

    fun buyToy(emoji: String, cost: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val success = repository.purchaseToy(emoji, cost)
            if (success) {
                onSuccess()
            } else {
                val profile = repository.getOrCreateProfile()
                if (profile.coins < cost) {
                    onError("Not enough coins! Solve reading words to earn!")
                } else {
                    onError("Already unlocked in your trophy room!")
                }
            }
        }
    }

    fun updateNickname(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.updateNickname(name.trim())
            }
        }
    }

    // ==========================================
    // Math Lesson: Unified Range-Selectable Arithmetic
    // ==========================================
    var mathQuizIndex by mutableStateOf(0)
    var mathScore by mutableStateOf(0)
    var mathQuestions = mutableListOf<MathQuestion>()
    var selectedMathOption by mutableStateOf<Int?>(null)
    var isMathAnswerChecked by mutableStateOf(false)
    var isMathAnswerCorrect by mutableStateOf(false)
    val totalMathQuizCount = 5

    // Selection properties
    var selectedMathOp by mutableStateOf("+") // "+", "-", "*", "/"
    var selectedRangeMin by mutableStateOf(0)
    var selectedRangeMax by mutableStateOf(10)

    fun selectMathOpAndNavigate(op: String) {
        selectedMathOp = op
        val profile = profileState.value ?: StudentProfile()
        val currentLevel = when (op) {
            "+" -> profile.additionLevel
            "-" -> profile.subtractionLevel
            "*" -> profile.multiplicationLevel
            else -> profile.divisionLevel
        }
        // Be helpful: pre-set range defaults based on the student's level
        when {
            currentLevel <= 3 -> {
                selectedRangeMin = 0
                selectedRangeMax = 10
            }
            currentLevel <= 6 -> {
                selectedRangeMin = 0
                selectedRangeMax = 20
            }
            currentLevel <= 8 -> {
                selectedRangeMin = 20
                selectedRangeMax = 50
            }
            else -> {
                selectedRangeMin = 50
                selectedRangeMax = 100
            }
        }
        navigateTo(Screen.LessonMathSetup)
    }

    fun generateMathQuest() {
        val op = selectedMathOp
        val min = selectedRangeMin
        val max = selectedRangeMax

        mathQuestions.clear()
        val countingEmojis = when (op) {
            "+" -> listOf("🍎", "🍓", "🍌", "🥕", "🏀", "🐞", "🚗", "🍬", "🦖", "🍦")
            "-" -> listOf("🍇", "🍐", "🍑", "🍒", "⚽️", "🧸", "✈️", "🍭", "🍩", "🍪")
            "*" -> listOf("🍏", "🍋", "🍍", "🥑", "🧁", "🍄", "⭐", "🎈", "🎲", "🧩")
            else -> listOf("🍊", "🍉", "🍒", "🥝", "🍩", "🔔", "🪁", "🎁", "🎨", "🧶")
        }

        for (i in 0 until totalMathQuizCount) {
            var val1 = 0
            var val2 = 0
            var correctAns = 0

            when (op) {
                "+" -> {
                    if (min == 0) {
                        val1 = Random.nextInt(0, max + 1)
                        val2 = Random.nextInt(0, (max - val1).coerceAtLeast(1))
                    } else {
                        val1 = Random.nextInt(min, max - 1)
                        val2 = Random.nextInt(1, (max - val1).coerceAtLeast(2))
                    }
                    correctAns = val1 + val2
                }
                "-" -> {
                    if (min == 0) {
                        val1 = Random.nextInt(1, max + 1)
                        val2 = Random.nextInt(0, val1 + 1)
                    } else {
                        val1 = Random.nextInt(min + 2, max + 1)
                        val2 = Random.nextInt(1, (val1 - min).coerceAtLeast(2))
                    }
                    correctAns = val1 - val2
                }
                "*" -> {
                    var success = false
                    var attempts = 0
                    val localMin = if (min == 0) 1 else min
                    while (!success && attempts < 100) {
                        val1 = Random.nextInt(1, (max / 2 + 1).coerceAtLeast(3))
                        val2 = Random.nextInt(1, (max / 2 + 1).coerceAtLeast(3))
                        correctAns = val1 * val2
                        if (correctAns in localMin..max) {
                            success = true
                        }
                        attempts++
                    }
                    if (!success) {
                        val1 = 2
                        val2 = 3
                        correctAns = 6
                    }
                }
                "/" -> {
                    val localMin = if (min == 0) 1 else min
                    correctAns = Random.nextInt(localMin, max + 1)
                    val2 = when {
                        correctAns > 20 -> Random.nextInt(1, 3)
                        correctAns > 10 -> Random.nextInt(1, 4)
                        else -> Random.nextInt(2, 6)
                    }
                    val1 = correctAns * val2
                }
            }

            // Generate distractors
            val optionsSet = mutableSetOf(correctAns)
            while (optionsSet.size < 4) {
                val offset = Random.nextInt(-5, 6)
                val distractor = correctAns + offset
                if (distractor >= 0 && distractor != correctAns && distractor <= (max * 1.5).toInt()) {
                    optionsSet.add(distractor)
                }
            }

            val emoji = countingEmojis.random()
            mathQuestions.add(
                MathQuestion(
                    val1 = val1,
                    val2 = val2,
                    op = op,
                    correctAnswer = correctAns,
                    options = optionsSet.toList().shuffled(),
                    emojiCountVal1 = emoji,
                    emojiCountVal2 = emoji
                )
            )
        }

        mathQuizIndex = 0
        mathScore = 0
        selectedMathOption = null
        isMathAnswerChecked = false
        isMathAnswerCorrect = false
    }

    fun submitMathAnswer(selectedOption: Int) {
        selectedMathOption = selectedOption
        isMathAnswerChecked = true
        val currentQ = mathQuestions[mathQuizIndex]
        if (selectedOption == currentQ.correctAnswer) {
            isMathAnswerCorrect = true
            mathScore++
        } else {
            isMathAnswerCorrect = false
        }
    }

    fun nextMathQuestion() {
        if (mathQuizIndex < totalMathQuizCount - 1) {
            mathQuizIndex++
            selectedMathOption = null
            isMathAnswerChecked = false
            isMathAnswerCorrect = false
        } else {
            val type = when (selectedMathOp) {
                "+" -> "addition"
                "-" -> "subtraction"
                "*" -> "multiplication"
                else -> "division"
            }
            viewModelScope.launch {
                repository.updateLessonProgress(
                    lessonType = type,
                    correctAnswers = mathScore,
                    totalQuestions = totalMathQuizCount,
                    solvedInc = mathScore
                )
                navigateTo(Screen.Dashboard)
            }
        }
    }

    // ==========================================
    // Math Lesson: Find Missing Number state & code
    // ==========================================
    var missingQuestions = mutableListOf<MissingNumberQuestion>()
    var selectedMissingOption by mutableStateOf<Int?>(null)
    var isMissingAnswerChecked by mutableStateOf(false)
    var isMissingAnswerCorrect by mutableStateOf(false)

    fun generateMissingNumberLesson() {
        val profile = profileState.value ?: StudentProfile()
        val limit = when {
            profile.missingNumberLevel <= 3 -> 12
            profile.missingNumberLevel <= 6 -> 30
            else -> 60
        }

        missingQuestions.clear()
        for (i in 0 until totalMathQuizCount) {
            val isAddition = Random.nextBoolean()
            if (isAddition) {
                // A + B = C
                val a = Random.nextInt(1, limit / 2 + 1)
                val b = Random.nextInt(1, limit / 2 + 1)
                val c = a + b
                val missingFirst = Random.nextBoolean()

                if (missingFirst) {
                    // ❓ + B = C
                    val options = generateDistractors(a, 1, limit)
                    missingQuestions.add(
                        MissingNumberQuestion(
                            val1 = null,
                            val2 = b,
                            op = "+",
                            displayString = "❓ + $b = $c",
                            correctAnswer = a,
                            options = options
                        )
                    )
                } else {
                    // A + ❓ = C
                    val options = generateDistractors(b, 1, limit)
                    missingQuestions.add(
                        MissingNumberQuestion(
                            val1 = a,
                            val2 = null,
                            op = "+",
                            displayString = "$a + ❓ = $c",
                            correctAnswer = b,
                            options = options
                        )
                    )
                }
            } else {
                // A - B = C -> ensure no negatives
                val a = Random.nextInt(3, limit + 1)
                val b = Random.nextInt(1, a)
                val c = a - b
                val missingFirst = Random.nextBoolean()

                if (missingFirst) {
                    // ❓ - B = C
                    val options = generateDistractors(a, b, limit + 5)
                    missingQuestions.add(
                        MissingNumberQuestion(
                            val1 = null,
                            val2 = b,
                            op = "-",
                            displayString = "❓ - $b = $c",
                            correctAnswer = a,
                            options = options
                        )
                    )
                } else {
                    // A - ❓ = C
                    val options = generateDistractors(b, 1, a)
                    missingQuestions.add(
                        MissingNumberQuestion(
                            val1 = a,
                            val2 = null,
                            op = "-",
                            displayString = "$a - ❓ = $c",
                            correctAnswer = b,
                            options = options
                        )
                    )
                }
            }
        }

        mathQuizIndex = 0
        mathScore = 0
        selectedMissingOption = null
        isMissingAnswerChecked = false
        isMissingAnswerCorrect = false
    }

    private fun generateDistractors(correct: Int, min: Int, max: Int): List<Int> {
        val optionsSet = mutableSetOf(correct)
        while (optionsSet.size < 4) {
            val dist = correct + Random.nextInt(-4, 5)
            if (dist in min..max && dist != correct) {
                optionsSet.add(dist)
            }
        }
        return optionsSet.toList().shuffled()
    }

    fun submitMissingAnswer(option: Int) {
        selectedMissingOption = option
        isMissingAnswerChecked = true
        val current = missingQuestions[mathQuizIndex]
        if (option == current.correctAnswer) {
            isMissingAnswerCorrect = true
            mathScore++
        } else {
            isMissingAnswerCorrect = false
        }
    }

    fun nextMissingQuestion() {
        if (mathQuizIndex < totalMathQuizCount - 1) {
            mathQuizIndex++
            selectedMissingOption = null
            isMissingAnswerChecked = false
            isMissingAnswerCorrect = false
        } else {
            viewModelScope.launch {
                repository.updateLessonProgress(
                    lessonType = "missing",
                    correctAnswers = mathScore,
                    totalQuestions = totalMathQuizCount,
                    solvedInc = mathScore
                )
                navigateTo(Screen.Dashboard)
            }
        }
    }

    // ==========================================
    // Math Lesson: Simple Mini Sudoku state & rules
    // ==========================================
    // We generate a 4x4 mini-sudoku board which contains numbers 1, 2, 3, 4.
    // It's exceptionally well-suited for 7-8 year olds. Easy level starts with 3 predefined slots missing,
    // Medium has 5 missing, Hard has 7 missing.
    var activeSudokuBoard by mutableStateOf<SudokuBoard?>(null)
    var sudokuSelectedCellId by mutableStateOf<Int?>(null) // index in board list

    fun generateSudokuQuiz() {
        val profile = profileState.value ?: StudentProfile()
        val numBlanks = when {
            profile.sudokuLevel <= 3 -> 2 // Super easy entry point
            profile.sudokuLevel <= 6 -> 4
            else -> 6
        }

        // A valid 4x4 Latin Square that forms a mini Sudoku (with subgrids)
        val solvedPattern = listOf(
            listOf(1, 2, 3, 4),
            listOf(3, 4, 1, 2),
            listOf(2, 1, 4, 3),
            listOf(4, 3, 2, 1)
        )

        // Shuffle numbers mapping to generate different valid boards
        val nums = (1..4).shuffled()
        val mappedSolvedGrid = solvedPattern.map { row ->
            row.map { num -> nums[num - 1] }
        }

        // Generate cells list
        val cells = mutableListOf<SudokuCell>()
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                cells.add(
                    SudokuCell(
                        row = r,
                        col = c,
                        correctValue = mappedSolvedGrid[r][c],
                        currentValue = mappedSolvedGrid[r][c],
                        isGiven = true
                    )
                )
            }
        }

        // Poke holes for user to fill
        val selectedHoles = mutableSetOf<Int>()
        while (selectedHoles.size < numBlanks) {
            val randIdx = Random.nextInt(0, 16)
            selectedHoles.add(randIdx)
        }

        for (idx in selectedHoles) {
            val orig = cells[idx]
            cells[idx] = orig.copy(currentValue = 0, isGiven = false)
        }

        activeSudokuBoard = SudokuBoard(cells)
        sudokuSelectedCellId = null
    }

    fun fillSudokuCell(cellIndex: Int, value: Int) {
        val board = activeSudokuBoard ?: return
        val updatedCells = board.cells.mapIndexed { index, cell ->
            if (index == cellIndex && !cell.isGiven) {
                cell.copy(currentValue = value)
            } else {
                cell
            }
        }
        activeSudokuBoard = board.copy(cells = updatedCells)
    }

    fun checkSudokuResult(onComplete: (Boolean) -> Unit) {
        val board = activeSudokuBoard ?: return
        var allCorrect = true
        for (cell in board.cells) {
            if (cell.currentValue != cell.correctValue) {
                allCorrect = false
                break
            }
        }

        if (allCorrect) {
            viewModelScope.launch {
                repository.updateLessonProgress(
                    lessonType = "sudoku",
                    correctAnswers = 1,
                    totalQuestions = 1,
                    solvedInc = 0
                )
                onComplete(true)
            }
        } else {
            onComplete(false)
        }
    }

    // ==========================================
    // Reading Lesson: Cute Emoji Matching Game
    // ==========================================
    // Match written name cards (on the left) to emoji pictures (on the right).
    var matchRound by mutableStateOf<WordMatchingRound?>(null)
    var selectedLeftWord by mutableStateOf<String?>(null)
    var selectedRightEmoji by mutableStateOf<String?>(null)
    var matchScoreCount by mutableStateOf(0)

    // Data assets
    private val readingCorpus = listOf(
        WordMatchItem("CAT", "🐱"),
        WordMatchItem("DOG", "🐶"),
        WordMatchItem("PIG", "🐷"),
        WordMatchItem("COW", "🐮"),
        WordMatchItem("APPLE", "🍎"),
        WordMatchItem("BANANA", "🍌"),
        WordMatchItem("CAR", "🚗"),
        WordMatchItem("BABY", "👶"),
        WordMatchItem("STAR", "⭐️"),
        WordMatchItem("SUN", "☀️"),
        WordMatchItem("MOON", "🌙"),
        WordMatchItem("CAKE", "🍰"),
        WordMatchItem("MILK", "🥛"),
        WordMatchItem("ROCKET", "🚀"),
        WordMatchItem("DINO", "🦖"),
        WordMatchItem("FISH", "🐟"),
        WordMatchItem("BIRD", "🐦"),
        WordMatchItem("BEE", "🐝"),
        WordMatchItem("TREE", "🌳"),
        WordMatchItem("FLOWER", "🌸"),
        WordMatchItem("BALL", "⚽️"),
        WordMatchItem("BOOK", "📚"),
        WordMatchItem("CLOUD", "☁️"),
        WordMatchItem("FROG", "🐸"),
        WordMatchItem("LION", "🦁")
    )

    fun generateWordMatchingLesson() {
        val shuffledPool = readingCorpus.shuffled()
        // Capture 4 distinct pairs for matching
        val roundPairs = shuffledPool.take(4).map { it.copy() }
        
        val leftWords = roundPairs.map { it.word }.shuffled()
        val rightEmojis = roundPairs.map { it.emoji }.shuffled()

        matchRound = WordMatchingRound(leftWords, rightEmojis, roundPairs)
        selectedLeftWord = null
        selectedRightEmoji = null
        matchScoreCount = 0
    }

    fun selectLeftWordCard(word: String) {
        selectedLeftWord = word
        checkMatchPair()
    }

    fun selectRightEmojiCard(emoji: String) {
        selectedRightEmoji = emoji
        checkMatchPair()
    }

    private fun checkMatchPair() {
        val left = selectedLeftWord
        val right = selectedRightEmoji
        val round = matchRound ?: return

        if (left != null && right != null) {
            // Find correct relation
            val actualPair = round.pairs.find { it.word == left && it.emoji == right }
            if (actualPair != null) {
                // IT'S A MATCH!
                actualPair.isMatched = true
                matchScoreCount++
                
                // Clear selection
                selectedLeftWord = null
                selectedRightEmoji = null

                // Check if all 4 are completed
                if (matchScoreCount >= 4) {
                    viewModelScope.launch {
                        repository.updateLessonProgress(
                            lessonType = "wordMatch",
                            correctAnswers = 4,
                            totalQuestions = 4,
                            solvedInc = 4
                        )
                        delay(1200) // let sound or visual tick celebrate
                        navigateTo(Screen.Dashboard)
                    }
                }
            } else {
                // Incorrect match, reset selection
                selectedLeftWord = null
                selectedRightEmoji = null
            }
        }
    }

    // ==========================================
    // Reading Lesson: Spelling Word Puzzle
    // ==========================================
    // Look at active emoji, assemble letter buttons in correct sequence!
    var spellingPuzzleState by mutableStateOf<SpellingPuzzle?>(null)
    var spellingScore by mutableStateOf(0)
    var spellingQuizIndex by mutableStateOf(0)
    val totalSpellingQuizzes = 4
    val spellingWordPool = listOf(
        Pair("CAT", "🐱"),
        Pair("DOG", "🐶"),
        Pair("SUN", "☀️"),
        Pair("BEE", "🐝"),
        Pair("PIG", "🐷"),
        Pair("FISH", "🐟"),
        Pair("BIRD", "🐦"),
        Pair("LION", "🦁"),
        Pair("CAR", "🚗"),
        Pair("BALL", "⚽️"),
        Pair("BOOK", "📚"),
        Pair("FROG", "🐸"),
        Pair("CAKE", "🍰"),
        Pair("COW", "🐮"),
        Pair("MILK", "🥛"),
        Pair("TREE", "🌳"),
        Pair("STAR", "⭐️"),
        Pair("MOON", "🌙")
    )

    fun generateSpellingLesson() {
        spellingScore = 0
        spellingQuizIndex = 0
        loadSpellingRound()
    }

    private fun loadSpellingRound() {
        val item = spellingWordPool.shuffled().first()
        val word = item.first
        val emoji = item.second
        
        // Split word into absolute chars
        val chars = word.map { it.toString() }
        
        // Add random filler letter if word is very short (to challenge them)
        val finalLetters = chars.toMutableList()
        val alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        while (finalLetters.size < chars.size + 1 && finalLetters.size < 6) {
            val randCh = alphas.random().toString()
            if (!chars.contains(randCh)) {
                finalLetters.add(randCh)
            }
        }

        spellingPuzzleState = SpellingPuzzle(
            targetWord = word,
            targetEmoji = emoji,
            jumbledLetters = finalLetters.shuffled(),
            currentAnswerList = emptyList()
        )
    }

    fun tapSpellingLetter(letter: String) {
        val puzzle = spellingPuzzleState ?: return
        
        // If this letter was already tapped (part of current answer list), skip or remove it?
        // Let's implement tape-to-append behavior
        val countInAnswer = puzzle.currentAnswerList.count { it == letter }
        val countInJumbled = puzzle.jumbledLetters.count { it == letter }
        
        if (countInAnswer < countInJumbled) {
            val updatedAnswerList = puzzle.currentAnswerList + letter
            spellingPuzzleState = puzzle.copy(currentAnswerList = updatedAnswerList)
            
            // Check completed answer
            val totalLengthNeeded = puzzle.targetWord.length
            if (updatedAnswerList.size == totalLengthNeeded) {
                val reconstructedStr = updatedAnswerList.joinToString("")
                if (reconstructedStr == puzzle.targetWord) {
                    // Correct spelling!
                    spellingScore++
                    viewModelScope.launch {
                        delay(1000)
                        advanceSpellingPuzzle()
                    }
                } else {
                    // Wrong spelling - clear letters to try again!
                    viewModelScope.launch {
                        delay(800)
                        spellingPuzzleState = puzzle.copy(currentAnswerList = emptyList())
                    }
                }
            }
        }
    }

    fun clearSpellingAnswer() {
        val puzzle = spellingPuzzleState ?: return
        spellingPuzzleState = puzzle.copy(currentAnswerList = emptyList())
    }

    private fun advanceSpellingPuzzle() {
        if (spellingQuizIndex < totalSpellingQuizzes - 1) {
            spellingQuizIndex++
            loadSpellingRound()
        } else {
            viewModelScope.launch {
                repository.updateLessonProgress(
                    lessonType = "spelling",
                    correctAnswers = spellingScore,
                    totalQuestions = totalSpellingQuizzes,
                    solvedInc = spellingScore
                )
                navigateTo(Screen.Dashboard)
            }
        }
    }

    // ==========================================
    // Interactive Minigame: Math Bubble Pop
    // ==========================================
    // Bubble items represent simple math formulas. Pop them fast!
    var bubbleList by mutableStateOf<List<BubbleItem>>(emptyList())
    var bubbleGameActive by mutableStateOf(false)
    var bubbleTargetVal by mutableStateOf(0) // Bubble with this solution is correct!
    var bubbleScore by mutableStateOf(0)
    var bubbleTimerSeconds by mutableStateOf(30)
    private var bubbleJob: Job? = null

    fun startBubblePopMinigame() {
        bubbleList = emptyList()
        bubbleGameActive = true
        bubbleScore = 0
        bubbleTimerSeconds = 30
        generateNewBubbleTarget()
        
        // Launch dynamic engine loops for timer & scrolling bubbles
        bubbleJob?.cancel()
        bubbleJob = viewModelScope.launch {
            var msElapsed = 0
            while (bubbleTimerSeconds > 0 && bubbleGameActive) {
                delay(100)
                msElapsed += 100
                
                // Every 1 second, decrement timer
                if (msElapsed % 1000 == 0) {
                    bubbleTimerSeconds--
                }

                // Every 1.5 seconds, spawn a new bubble
                if (msElapsed % 1500 == 0) {
                    spawnNewBubble()
                }

                // Move all bubbles down or up!
                // We let them float UPWARDS by altering local list copy safely
                val currentBubblesList = bubbleList.toMutableList()
                val iterator = currentBubblesList.listIterator()
                while (iterator.hasNext()) {
                    val bubble = iterator.next()
                    val nextY = bubble.yPosition - 0.025f // float upwards
                    if (nextY < -0.15f) {
                        // bubble exited screen, remove it
                        iterator.remove()
                    } else {
                        iterator.set(bubble.copy(yPosition = nextY))
                    }
                }
                bubbleList = currentBubblesList
            }
            
            // Out of time -> award coins based on popped correct score
            bubbleGameActive = false
            val coinsEarned = bubbleScore * 2
            repository.addCoins(coinsEarned)
        }
    }

    private fun generateNewBubbleTarget() {
        // Generate random addition target within 1-12
        bubbleTargetVal = Random.nextInt(3, 13)
        // Ensure some bubbles match this
        spawnNewBubble()
        spawnNewBubble()
    }

    private fun spawnNewBubble() {
        val bubbleId = Random.nextInt(10000)
        val xOffset = Random.nextFloat() * 0.8f + 0.1f // center it within boundaries
        
        // 50% chance of being correct answer
        val isCorrect = Random.nextBoolean()
        var val1 = 0
        var val2 = 0
        var mathSolution = 0
        var op = "+"

        if (isCorrect) {
            // Formula must equal bubbleTargetVal
            mathSolution = bubbleTargetVal
            val1 = Random.nextInt(1, bubbleTargetVal)
            val2 = bubbleTargetVal - val1
            op = "+"
        } else {
            // Formula is random distractor
            mathSolution = Random.nextInt(3, 15)
            while (mathSolution == bubbleTargetVal) {
                mathSolution = Random.nextInt(3, 15)
            }
            val1 = Random.nextInt(1, mathSolution)
            val2 = mathSolution - val1
            op = "+"
        }

        // Color coding themes
        val bubbleHexes = listOf("#4FC3F7", "#81C784", "#FFD54F", "#FF8A65", "#BA68C8", "#F06292")

        val currentBubblesList = bubbleList.toMutableList()
        currentBubblesList.add(
            BubbleItem(
                id = bubbleId,
                questionHex = bubbleHexes.random(),
                formula = "$val1 $op $val2",
                value = mathSolution,
                isCorrect = isCorrect,
                xOffsetFraction = xOffset,
                yPosition = 1.1f // start below bottom of container
            )
        )
        bubbleList = currentBubblesList
    }

    fun popBubble(bubble: BubbleItem) {
        if (!bubbleGameActive) return
        
        val currentBubblesList = bubbleList.toMutableList()
        val idx = currentBubblesList.indexOfFirst { it.id == bubble.id }
        if (idx != -1 && !currentBubblesList[idx].isPopped) {
            currentBubblesList[idx] = currentBubblesList[idx].copy(isPopped = true)
            bubbleList = currentBubblesList
            
            if (bubble.isCorrect) {
                bubbleScore++
                // Generate a new target solution to keep it interesting!
                generateNewBubbleTarget()
            } else {
                // Penalty! Avoid picking wrong bubbles
                if (bubbleScore > 0) bubbleScore--
            }
        }
    }

    // ==========================================
    // Cube Counting Lesson state & code
    // ==========================================
    var cubeQuizIndex by mutableStateOf(0)
    var cubeScore by mutableStateOf(0)
    var cubeQuestions by mutableStateOf<List<CubeStackQuestion>>(emptyList())
    var selectedCubeOption by mutableStateOf<Int?>(null)
    var isCubeAnswerChecked by mutableStateOf(false)
    var isCubeAnswerCorrect by mutableStateOf(false)
    val totalCubeQuizCount = 5

    fun generateCubeCountingLesson() {
        val profile = profileState.value ?: StudentProfile()
        val level = profile.cubeCountingLevel
        
        val list = when {
            level <= 3 -> {
                // Easy
                listOf(
                    CubeStackQuestion(
                        difficulty = "Easy",
                        columns = listOf(CubeColumn(0, 0, 3)),
                        correctAnswer = 3,
                        options = listOf(2, 3, 4, 5),
                        hint = "Count the vertical blocks in the single tower: 1, 2, 3!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Easy",
                        columns = listOf(CubeColumn(0, 0, 2), CubeColumn(0, 1, 1)),
                        correctAnswer = 3,
                        options = listOf(3, 4, 5, 2),
                        hint = "One tower of 2, and one tower of 1. 2 + 1 = 3!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Easy",
                        columns = listOf(CubeColumn(0, 0, 2), CubeColumn(0, 1, 2), CubeColumn(0, 2, 1)),
                        correctAnswer = 5,
                        options = listOf(4, 5, 6, 7),
                        hint = "There are two towers of 2, and one tower of 1. 2 + 2 + 1 = 5!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Easy",
                        columns = listOf(CubeColumn(0, 0, 3), CubeColumn(0, 1, 2)),
                        correctAnswer = 5,
                        options = listOf(3, 4, 5, 6),
                        hint = "Count each block: 3 in the first column, plus 2 in the second column!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Easy",
                        columns = listOf(CubeColumn(0, 0, 1), CubeColumn(0, 1, 2), CubeColumn(0, 2, 1)),
                        correctAnswer = 4,
                        options = listOf(2, 3, 4, 5),
                        hint = "Look at the columns from left to right: 1 + 2 + 1 = 4 cubes!"
                    )
                )
            }
            level <= 6 -> {
                // Medium
                listOf(
                    CubeStackQuestion(
                        difficulty = "Medium",
                        columns = listOf(CubeColumn(0, 0, 2), CubeColumn(1, 0, 1), CubeColumn(0, 1, 1)),
                        correctAnswer = 4,
                        options = listOf(3, 4, 5, 6),
                        hint = "One tower of 2, and two separate single cubes. 2 + 1 + 1 = 4!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Medium",
                        columns = listOf(CubeColumn(0, 0, 3), CubeColumn(1, 0, 2), CubeColumn(2, 0, 1)),
                        correctAnswer = 6,
                        options = listOf(5, 6, 7, 8),
                        hint = "Stairs stack: 3 in the tallest, 2 in the middle, 1 in the front. 3 + 2 + 1 = 6!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Medium",
                        columns = listOf(CubeColumn(0, 0, 2), CubeColumn(1, 0, 1), CubeColumn(0, 1, 2), CubeColumn(1, 1, 1)),
                        correctAnswer = 6,
                        options = listOf(4, 5, 6, 7),
                        hint = "Two towers of height 2 and two towers of height 1. 2 + 2 + 1 + 1 = 6!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Medium",
                        columns = listOf(CubeColumn(0, 0, 2), CubeColumn(0, 1, 2), CubeColumn(0, 2, 2), CubeColumn(1, 1, 1)),
                        correctAnswer = 7,
                        options = listOf(6, 7, 8, 9),
                        hint = "Three towers of 2 at the back (total 6) plus one single cube in front. 6 + 1 = 7!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Medium",
                        columns = listOf(CubeColumn(0, 0, 3), CubeColumn(0, 1, 3), CubeColumn(1, 0, 1), CubeColumn(1, 1, 1)),
                        correctAnswer = 8,
                        options = listOf(6, 7, 8, 9),
                        hint = "Two back towers of 3 (total 6) and two front blocks of 1 (total 2). 6 + 2 = 8!"
                    )
                )
            }
            else -> {
                // Hard
                listOf(
                    CubeStackQuestion(
                        difficulty = "Hard",
                        columns = listOf(CubeColumn(0, 0, 3), CubeColumn(1, 0, 2), CubeColumn(0, 1, 2), CubeColumn(1, 1, 1)),
                        correctAnswer = 8,
                        options = listOf(6, 7, 8, 9),
                        hint = "Pyramid stack: back tower is 3, sides are 2, and front is 1. 3 + 2 + 2 + 1 = 8!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Hard",
                        columns = listOf(CubeColumn(0, 0, 4), CubeColumn(0, 1, 2), CubeColumn(0, 2, 4), CubeColumn(1, 0, 1), CubeColumn(1, 1, 1), CubeColumn(1, 2, 1)),
                        correctAnswer = 13,
                        options = listOf(11, 12, 13, 14),
                        hint = "Two towers of 4 (total 8), a middle tower of 2 (total 10), and three front blocks of 1 (total 13)!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Hard",
                        columns = listOf(CubeColumn(0, 0, 4), CubeColumn(1, 0, 3), CubeColumn(0, 1, 2), CubeColumn(1, 1, 1)),
                        correctAnswer = 10,
                        options = listOf(8, 9, 10, 11),
                        hint = "Let's count columns back to front: tallest column has 4, next is 3, next is 2, front is 1. 4 + 3 + 2 + 1 = 10!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Hard",
                        columns = listOf(CubeColumn(0, 0, 3), CubeColumn(0, 1, 3), CubeColumn(0, 2, 3), CubeColumn(1, 1, 2)),
                        correctAnswer = 11,
                        options = listOf(9, 10, 11, 12),
                        hint = "Three back columns of 3 (total 9) and one column of 2 in front. 9 + 2 = 11!"
                    ),
                    CubeStackQuestion(
                        difficulty = "Hard",
                        columns = listOf(
                            CubeColumn(0, 0, 3), CubeColumn(0, 2, 3), CubeColumn(0, 1, 1),
                            CubeColumn(1, 0, 2), CubeColumn(1, 2, 2), CubeColumn(1, 1, 1)
                        ),
                        correctAnswer = 12,
                        options = listOf(10, 11, 12, 13),
                        hint = "Corner towers of 3 (total 6), side-ends of 2 (total 4), and single blocks in middle (total 2). 6 + 4 + 2 = 12!"
                    )
                )
            }
        }
        
        cubeQuestions = list
        cubeQuizIndex = 0
        cubeScore = 0
        selectedCubeOption = null
        isCubeAnswerChecked = false
        isCubeAnswerCorrect = false
    }

    fun submitCubeAnswer(option: Int) {
        selectedCubeOption = option
        isCubeAnswerChecked = true
        val currentQ = cubeQuestions.getOrNull(cubeQuizIndex) ?: return
        if (option == currentQ.correctAnswer) {
            isCubeAnswerCorrect = true
            cubeScore++
        } else {
            isCubeAnswerCorrect = false
        }
    }

    fun nextCubeQuestion() {
        if (cubeQuizIndex < totalCubeQuizCount - 1) {
            cubeQuizIndex++
            selectedCubeOption = null
            isCubeAnswerChecked = false
            isCubeAnswerCorrect = false
        } else {
            // Save Lesson Progress and navigate back to Dashboard
            viewModelScope.launch {
                repository.updateLessonProgress(
                    lessonType = "cubes",
                    correctAnswers = cubeScore,
                    totalQuestions = totalCubeQuizCount,
                    solvedInc = cubeScore
                )
                navigateTo(Screen.Dashboard)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bubbleJob?.cancel()
    }
}

package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ArithmeticQuizScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val qIndex = viewModel.mathQuizIndex
    val totalCount = viewModel.totalMathQuizCount
    val score = viewModel.mathScore

    val questions = viewModel.mathQuestions
    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ColorGreenPrimary)
        }
        return
    }

    val currentQ = questions[qIndex]

    Scaffold(
        topBar = {
            TopBarHelper(
                title = when (viewModel.selectedMathOp) {
                    "+" -> "Magic Addition Quest ➕"
                    "-" -> "Super Subtraction Quest ➖"
                    "*" -> "Cool Multiplication Quest ✖️"
                    else -> "Clever Division Quest ➗"
                },
                onBack = onBack
            )
        },
        containerColor = ColorBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // PROGRESS HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${qIndex + 1} of $totalCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Points",
                        tint = ColorTealProgress,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "$score Stars",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                }
            }

            // VISUAL COUNTER PANEL (Naturally Toned)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(24.dp))
                    .background(ColorCreamSurface, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Let's count! 🍎🍏",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorText.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                OptInFlowRowHelper(
                    currentQ = currentQ,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Numerical written state
                Text(
                    text = "${currentQ.val1} ${currentQ.op} ${currentQ.val2} = ?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorHeading
                )
            }

            // SELECTION BUBBLES
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!viewModel.isMathAnswerChecked) {
                    Text(
                        text = "Pop the correct answer bubble below! 👇",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorHeading
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentQ.options.forEach { option ->
                        val isSelected = viewModel.selectedMathOption == option
                        val isCorrectOption = option == currentQ.correctAnswer
                        
                        val (bubbleColor, borderBubbleColor) = when {
                            viewModel.isMathAnswerChecked && isCorrectOption -> Pair(ColorGreenContainer, ColorGreenPrimary)
                            viewModel.isMathAnswerChecked && isSelected -> Pair(Color(0xFFF9E8E8), Color(0xFFE2C4C4))
                            isSelected -> Pair(ColorTealBg, ColorTealProgress)
                            else -> Pair(ColorCreamSurface, ColorCreamBorder)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(bubbleColor)
                                .border(1.dp, borderBubbleColor, CircleShape)
                                .clickable(enabled = !viewModel.isMathAnswerChecked) {
                                    viewModel.submitMathAnswer(option)
                                }
                                .testTag("math_option_bubble_$option")
                        ) {
                            Text(
                                text = "$option",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorHeading
                            )
                        }
                    }
                }
            }

            // INTERACTIVE FOOTER RESPONSE BOX
            AnimatedVisibility(
                visible = viewModel.isMathAnswerChecked,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (viewModel.isMathAnswerCorrect) ColorGreenPrimary else Color(0xFFE2C4C4)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (viewModel.isMathAnswerCorrect) ColorGreenContainer else Color(0xFFF9E8E8)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (viewModel.isMathAnswerCorrect) "Correct! Brilliant job! 🎉🏆" else "Oops, let's look at the counts and try again! 🌟",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = ColorHeading,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.nextMathQuestion() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isMathAnswerCorrect) ColorGreenPrimary else ColorHeading
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (qIndex < totalCount - 1) "Next Question ➡️" else "Finish Quest 🏁",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissingNumQuizScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val qIndex = viewModel.mathQuizIndex
    val totalCount = viewModel.totalMathQuizCount
    val score = viewModel.mathScore

    val questions = viewModel.missingQuestions
    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ColorGreenPrimary)
        }
        return
    }

    val currentQ = questions[qIndex]

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Secret Shapes & Numbers ❓",
                onBack = onBack
            )
        },
        containerColor = ColorBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // score progress info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Puzzle ${qIndex + 1} of $totalCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Points",
                        tint = ColorTealProgress,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "$score Stars",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                }
            }

            // MAIN EQUATION PANEL (Styled Slate blackboard for highest visibility)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorHeading) // sleek slate slate blackboard style
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fill back the missing number!",
                        fontSize = 14.sp,
                        color = ColorBg,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Styled chalkboard equation
                    Text(
                        text = currentQ.displayString.replace("❓", if (viewModel.isMissingAnswerChecked) "${currentQ.correctAnswer}" else "❓"),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // OPTION BUBBLE DRAWER
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!viewModel.isMissingAnswerChecked) {
                    Text(
                        text = "Which key unlocks the formula?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentQ.options.forEach { option ->
                        val isSelected = viewModel.selectedMissingOption == option
                        val isCorrectOption = option == currentQ.correctAnswer

                        val (bubbleColor, borderBubbleColor) = when {
                            viewModel.isMissingAnswerChecked && isCorrectOption -> Pair(ColorGreenContainer, ColorGreenPrimary)
                            viewModel.isMissingAnswerChecked && isSelected -> Pair(Color(0xFFF9E8E8), Color(0xFFE2C4C4))
                            isSelected -> Pair(ColorTealBg, ColorTealProgress)
                            else -> Pair(ColorCreamSurface, ColorCreamBorder)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(bubbleColor)
                                .border(1.dp, borderBubbleColor, CircleShape)
                                .clickable(enabled = !viewModel.isMissingAnswerChecked) {
                                    viewModel.submitMissingAnswer(option)
                                }
                                .testTag("missing_option_$option")
                        ) {
                            Text(
                                text = "$option",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorHeading
                            )
                        }
                    }
                }
            }

            // FOOTER RESULT ACTIONS
            AnimatedVisibility(
                visible = viewModel.isMissingAnswerChecked,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (viewModel.isMissingAnswerCorrect) ColorGreenPrimary else Color(0xFFE2C4C4)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (viewModel.isMissingAnswerCorrect) ColorGreenContainer else Color(0xFFF9E8E8)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (viewModel.isMissingAnswerCorrect) "Amazing skill! You decoded it! 🔑🌟" else "Oh! Put the numbers on your fingers and try again!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = ColorHeading,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.nextMissingQuestion() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isMissingAnswerCorrect) ColorGreenPrimary else ColorHeading
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (qIndex < totalCount - 1) "Next ➡️" else "Finish Quest 🏁",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ===================================
// MINI 4X4 SUDOKU SCREEN FOR KIDS
// ===================================
@Composable
fun SudokuScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val board = viewModel.activeSudokuBoard
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isCorrectCelebration by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (board == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ColorGreenPrimary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "4x4 Mini Sudoku Board 🧠",
                onBack = onBack
            )
        },
        containerColor = ColorBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Rule: Fill numbers 1-4 so each column and row has different numbers! 🌟",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                // The 4x4 vertical grid layout (Styled as cream paper block)
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ColorCreamSurface)
                        .border(1.dp, ColorCreamBorder, RoundedCornerShape(24.dp))
                        .padding(8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(board.cells) { index, cell ->
                            val isSelected = viewModel.sudokuSelectedCellId == index
                            val cellBackground = when {
                                cell.isGiven -> ColorTealBg // standard pre-filled
                                isSelected -> ColorGreenContainer // highlighting
                                else -> ColorBg // normal slots
                            }
                            
                            val cellBorder = when {
                                isSelected -> ColorGreenPrimary
                                else -> ColorCreamBorder
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cellBackground)
                                    .border(1.dp, cellBorder, RoundedCornerShape(12.dp))
                                    .clickable(enabled = !cell.isGiven) {
                                        viewModel.sudokuSelectedCellId = index
                                    }
                                    .testTag("sudoku_cell_$index")
                            ) {
                                Text(
                                    text = if (cell.currentValue > 0) "${cell.currentValue}" else "❓",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cell.isGiven) ColorHeading else ColorGreenPrimary
                                )
                            }
                        }
                    }
                }
            }

            // USER INPUT KEYPAD (Drawer for numbers 1 to 4)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (viewModel.sudokuSelectedCellId != null) "Select a helper number below:" else "Tap a question mark cell above!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorHeading
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..4).forEach { valToInsert ->
                        Button(
                            onClick = {
                                val currentSelected = viewModel.sudokuSelectedCellId
                                if (currentSelected != null) {
                                    viewModel.fillSudokuCell(currentSelected, valToInsert)
                                }
                            },
                            enabled = viewModel.sudokuSelectedCellId != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorGreenPrimary,
                                disabledContainerColor = ColorCreamBorder
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(54.dp).testTag("sudoku_keypad_$valToInsert")
                        ) {
                            Text("$valToInsert", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // FINAL SOLUTIONS & FEEDBACK DIALOG
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (feedbackMessage != null) {
                    Text(
                        text = feedbackMessage!!,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrectCelebration) ColorGreenPrimary else Color(0xFF6D4242),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        viewModel.checkSudokuResult { success ->
                            if (success) {
                                isCorrectCelebration = true
                                feedbackMessage = "Superb Brain Mastery! Complete! ⭐🎉"
                                coroutineScope.launch {
                                    delay(1800)
                                    onBack()
                                }
                            } else {
                                isCorrectCelebration = false
                                feedbackMessage = "Ah! Some numbers clash. Review rows and columns! 🧠"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sudoku_check_button")
                ) {
                    Text("Check My Sudoku Answers! 🧠", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// PRIVATE HELPER TO PRESERVE COMPOSABLE LOCAL FLOWROW WITHOUT IMPORTS BLOCKS OR DEPRECATIONS
@Composable
private fun OptInFlowRowHelper(
    currentQ: com.example.ui.MathQuestion,
    viewModel: MainViewModel
) {
    if (currentQ.val1 > 15 || currentQ.val2 > 15) {
        // High numbers display - show clean numeric cards with single illustrative emojis
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(64.dp)
                    .background(ColorTealBg, RoundedCornerShape(16.dp))
                    .border(1.dp, ColorTealBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${currentQ.val1}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                    Text(text = currentQ.emojiCountVal1, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = currentQ.op,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorHeading
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(64.dp)
                    .background(ColorTealBg, RoundedCornerShape(16.dp))
                    .border(1.dp, ColorTealBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${currentQ.val2}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                    Text(text = currentQ.emojiCountVal2, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "=",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorHeading
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(ColorBg)
                    .border(1.dp, ColorCreamBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (viewModel.isMathAnswerChecked) "${currentQ.correctAnswer}" else "❓",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading
                )
            }
        }
    } else {
        // Under 15 display - draw standard individual interactive icons for kids
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.widthIn(max = 110.dp)
            ) {
                Text(
                    text = (1..currentQ.val1).joinToString(" ") { currentQ.emojiCountVal1 },
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = currentQ.op,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ColorHeading
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.widthIn(max = 110.dp)
            ) {
                Text(
                    text = (1..currentQ.val2).joinToString(" ") { currentQ.emojiCountVal2 },
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "=",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ColorHeading
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Sparkly target answer bubble
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(ColorBg)
                    .border(1.dp, ColorCreamBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (viewModel.isMathAnswerChecked) "${currentQ.correctAnswer}" else "❓",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading
                )
            }
        }
    }
}

private suspend fun delay(timeMillis: Long) {
    kotlinx.coroutines.delay(timeMillis)
}

// SHARED TOPBAR HELPER DEFINITION FOR THEMED HEADERS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarHelper(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = com.example.ui.theme.ColorHeading
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.testTag("top_bar_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    tint = com.example.ui.theme.ColorHeading
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = com.example.ui.theme.ColorCreamSurface)
    )
}

@Composable
fun MathSetupScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val operationSymbol = viewModel.selectedMathOp
    val operationName = when (operationSymbol) {
        "+" -> "Addition"
        "-" -> "Subtraction"
        "*" -> "Multiplication"
        else -> "Division"
    }

    val ranges = listOf(
        Pair(0, 10) to "Easy Explorer (0 to 10) 👶",
        Pair(0, 20) to "Rising Star (0 to 20) ⭐",
        Pair(20, 50) to "Smart Scout (20 to 50) 🔍",
        Pair(50, 100) to "Hero Mastermind (50 to 100) 👑"
    )

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Setup $operationName Practice",
                onBack = onBack
            )
        },
        containerColor = ColorBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Friendly visual header card
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorGreenContainer),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Customize Your Quest! 🎈",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose a level level-up range or customize your own numbers below!",
                        fontSize = 14.sp,
                        color = ColorHeading,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Quick Select Title
            Text(
                text = "⚡ Quick Pick Level Ranges",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorHeading,
                modifier = Modifier.align(Alignment.Start)
            )

            // Selectable Range Buttons
            ranges.forEach { (range, title) ->
                val isSelected = viewModel.selectedRangeMin == range.first && viewModel.selectedRangeMax == range.second
                Button(
                    onClick = {
                        viewModel.selectedRangeMin = range.first
                        viewModel.selectedRangeMax = range.second
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) ColorGreenPrimary else ColorCreamSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("range_${range.first}_to_${range.second}"),
                    border = BorderStroke(2.dp, if (isSelected) ColorGreenPrimary else ColorCreamBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else ColorHeading
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CUSTOM PICKER SECTION
            Text(
                text = "⚙️ Custom Number Maker",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorHeading,
                modifier = Modifier.align(Alignment.Start)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                border = BorderStroke(1.dp, ColorCreamBorder),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Min selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Minimum Number", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            Text("Start practicing from...", fontSize = 11.sp, color = ColorHeading.copy(alpha = 0.7f))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.selectedRangeMin = (viewModel.selectedRangeMin - 5).coerceAtLeast(0) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(ColorTealBg, CircleShape)
                                    .testTag("custom_min_minus")
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            }

                            Text(
                                text = "${viewModel.selectedRangeMin}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorHeading,
                                modifier = Modifier.widthIn(min = 28.dp),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = { viewModel.selectedRangeMin = (viewModel.selectedRangeMin + 5).coerceAtMost(viewModel.selectedRangeMax - 2) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(ColorTealBg, CircleShape)
                                    .testTag("custom_min_plus")
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            }
                        }
                    }

                    HorizontalDivider(color = ColorCreamBorder.copy(alpha = 0.5f))

                    // Max selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Maximum Number", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            Text("Practice numbers up to...", fontSize = 11.sp, color = ColorHeading.copy(alpha = 0.7f))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.selectedRangeMax = (viewModel.selectedRangeMax - 5).coerceAtLeast(viewModel.selectedRangeMin + 2) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(ColorTealBg, CircleShape)
                                    .testTag("custom_max_minus")
                            ) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            }

                            Text(
                                text = "${viewModel.selectedRangeMax}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorHeading,
                                modifier = Modifier.widthIn(min = 28.dp),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = { viewModel.selectedRangeMax = (viewModel.selectedRangeMax + 5).coerceAtMost(100) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(ColorTealBg, CircleShape)
                                    .testTag("custom_max_plus")
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Start adventure button
            Button(
                onClick = {
                    val targetScreen = when (operationSymbol) {
                        "+" -> Screen.LessonAddition
                        "-" -> Screen.LessonSubtraction
                        "*" -> Screen.LessonMultiplication
                        else -> Screen.LessonDivision
                    }
                    viewModel.navigateTo(targetScreen)
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("start_quest_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Let's Play! 🚀🎒", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

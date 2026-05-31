package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CubeCountingQuizScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val qIndex = viewModel.cubeQuizIndex
    val totalCount = viewModel.totalCubeQuizCount
    val score = viewModel.cubeScore

    val questions = viewModel.cubeQuestions
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
    var showHint by remember { mutableStateOf(false) }

    // Change block colors depending on complexity/difficulty
    val blockColor = when (currentQ.difficulty) {
        "Easy" -> Color(0xFF81C784) // Playful Green
        "Medium" -> Color(0xFF64B5F6) // Ocean Blue
        else -> Color(0xFFBA68C8) // Magic Purple
    }

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "3D Cube Stack Quest 📦",
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
                Column {
                    Text(
                        text = "Question ${qIndex + 1} of $totalCount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                    Text(
                        text = "Difficulty: ${currentQ.difficulty}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (currentQ.difficulty) {
                            "Easy" -> Color(0xFF4CAF50)
                            "Medium" -> Color(0xFF2196F3)
                            else -> Color(0xFF9C27B0)
                        }
                    )
                }
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

            // VISUAL 3D CANVAS BASEPLATE CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(vertical = 12.dp)
                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCreamSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "How many cubes are in the stack? 📦\nDon't forget the hidden support cubes!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorText.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Draw 3D Isometric stacks of blocks
                    val density = LocalDensity.current
                    val cubeSizePx = with(density) { 32.dp.toPx() }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Transparent)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Grid center projection formulas
                        val maxX = currentQ.columns.maxOfOrNull { it.x } ?: 0
                        val maxY = currentQ.columns.maxOfOrNull { it.y } ?: 0
                        val maxZ = currentQ.columns.maxOfOrNull { it.height } ?: 1

                        val x_mid = maxX / 2f
                        val y_mid = maxY / 2f

                        val angle = Math.toRadians(30.0)
                        val dx = cubeSizePx * Math.cos(angle).toFloat()
                        val dy = cubeSizePx * Math.sin(angle).toFloat()

                        val offsetX = canvasWidth / 2f - (y_mid - x_mid) * dx
                        val offsetY = canvasHeight / 2f - (x_mid + y_mid) * dy + (maxZ * cubeSizePx) / 2.5f

                        // Back plate grid to highlight isometrical field
                        drawBaseplateGrid(maxX + 1, maxY + 1, offsetX, offsetY, dx, dy, cubeSizePx)

                        // Draw stacked cubes layering
                        // Painter's algorithm: draw first lower height levels (z), then column coordinates back to front (x, y)
                        val columnsMap = currentQ.columns.associateBy { Pair(it.x, it.y) }

                        for (z in 0 until maxZ) {
                            for (x in 0..maxX) {
                                for (y in 0..maxY) {
                                    val col = columnsMap[Pair(x, y)]
                                    if (col != null && z < col.height) {
                                        val cx = offsetX + (y - x) * dx
                                        val cy = offsetY + (x + y) * dy - z * cubeSizePx
                                        drawIsometricCube(cx, cy, cubeSizePx, blockColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // OPTION BUBBLES
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!viewModel.isCubeAnswerChecked) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Choose the correct bubble below! 👇",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorHeading
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showHint = true },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("hint_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Show Hint",
                                tint = ColorGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentQ.options.forEach { option ->
                        val isSelected = viewModel.selectedCubeOption == option
                        val isCorrectOption = option == currentQ.correctAnswer

                        val (bubbleColor, borderBubbleColor) = when {
                            viewModel.isCubeAnswerChecked && isCorrectOption -> Pair(ColorGreenContainer, ColorGreenPrimary)
                            viewModel.isCubeAnswerChecked && isSelected -> Pair(Color(0xFFF9E8E8), Color(0xFFE2C4C4))
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
                                .clickable(enabled = !viewModel.isCubeAnswerChecked) {
                                    viewModel.submitCubeAnswer(option)
                                }
                                .testTag("cube_option_bubble_$option")
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

            // RESPONSE FEEDBACK PANEL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = viewModel.isCubeAnswerChecked,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (viewModel.isCubeAnswerCorrect) ColorGreenPrimary else Color(0xFFE2C4C4)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (viewModel.isCubeAnswerCorrect) ColorGreenContainer else Color(0xFFF9E8E8)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (viewModel.isCubeAnswerCorrect) "Correct! You have great 3D eyes! 🎉📦" else "Keep trying! Look at the support blocks underneath! 🌟",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = ColorHeading,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.nextCubeQuestion() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.isCubeAnswerCorrect) ColorGreenPrimary else ColorHeading
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (qIndex < totalCount - 1) "Next Stack ➡️" else "Finish Quest 🏁",
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

    // Interactive Hint Dialog
    if (showHint) {
        AlertDialog(
            onDismissRequest = { showHint = false },
            confirmButton = {
                TextButton(onClick = { showHint = false }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = ColorGreenPrimary)
                }
            },
            title = {
                Text("Architect Hint 💡", fontWeight = FontWeight.Bold, color = ColorHeading)
            },
            text = {
                Text(
                    text = currentQ.hint,
                    fontSize = 15.sp,
                    color = ColorText
                )
            },
            containerColor = ColorCreamSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// Draw a simple isometric ground grid baseplate for Lego style context
private fun DrawScope.drawBaseplateGrid(
    cols: Int,
    rows: Int,
    offsetX: Float,
    offsetY: Float,
    dx: Float,
    dy: Float,
    H: Float
) {
    val gridColor = ColorCreamBorder.copy(alpha = 0.5f)
    val baseplatePath = Path().apply {
        // Back-left corner (0,0) as relative top anchor of whole plate
        moveTo(offsetX, offsetY)
        lineTo(offsetX + rows * dx, offsetY + rows * dy)
        lineTo(offsetX + (rows - cols) * dx, offsetY + (rows + cols) * dy)
        lineTo(offsetX - cols * dx, offsetY + cols * dy)
        close()
    }
    drawPath(baseplatePath, color = ColorCreamBorder.copy(alpha = 0.15f))
    drawPath(baseplatePath, color = gridColor, style = Stroke(width = 2f))

    // Draw row dividing lines inside grid
    for (i in 1..rows) {
        drawPath(
            path = Path().apply {
                moveTo(offsetX + i * dx, offsetY + i * dy)
                lineTo(offsetX + i * dx - cols * dx, offsetY + i * dy + cols * dy)
            },
            color = gridColor,
            style = Stroke(width = 1.5f)
        )
    }

    // Draw column dividing lines inside grid
    for (j in 1..cols) {
        drawPath(
            path = Path().apply {
                moveTo(offsetX - j * dx, offsetY + j * dy)
                lineTo(offsetX - j * dx + rows * dx, offsetY + j * dy + rows * dy)
            },
            color = gridColor,
            style = Stroke(width = 1.5f)
        )
    }
}

// Pure mathematical 3D projection rendering helper for flawless shapes without coordinate overlap splits
private fun DrawScope.drawIsometricCube(
    cx: Float,
    cy: Float,
    H: Float,
    baseColor: Color
) {
    val angle = Math.toRadians(30.0)
    val dx = H * Math.cos(angle).toFloat()
    val dy = H * Math.sin(angle).toFloat()

    val topColor = baseColor
    val leftColor = Color(
        red = (baseColor.red * 0.82f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.82f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.82f).coerceIn(0f, 1f)
    )
    val rightColor = Color(
        red = (baseColor.red * 0.64f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.64f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.64f).coerceIn(0f, 1f)
    )
    val borderColor = Color(0xFF2D331C)

    // TOP FACE (Rhombus)
    val topPath = Path().apply {
        moveTo(cx, cy)
        lineTo(cx + dx, cy + dy)
        lineTo(cx, cy + (2 * dy))
        lineTo(cx - dx, cy + dy)
        close()
    }
    drawPath(topPath, color = topColor)
    drawPath(topPath, color = borderColor, style = Stroke(width = 1.5.dp.toPx()))

    // LEFT FACE
    val leftPath = Path().apply {
        moveTo(cx - dx, cy + dy)
        lineTo(cx, cy + (2 * dy))
        lineTo(cx, cy + (2 * dy) + H)
        lineTo(cx - dx, cy + dy + H)
        close()
    }
    drawPath(leftPath, color = leftColor)
    drawPath(leftPath, color = borderColor, style = Stroke(width = 1.5.dp.toPx()))

    // RIGHT FACE
    val rightPath = Path().apply {
        moveTo(cx, cy + (2 * dy))
        lineTo(cx + dx, cy + dy)
        lineTo(cx + dx, cy + dy + H)
        lineTo(cx, cy + (2 * dy) + H)
        close()
    }
    drawPath(rightPath, color = rightColor)
    drawPath(rightPath, color = borderColor, style = Stroke(width = 1.5.dp.toPx()))
}

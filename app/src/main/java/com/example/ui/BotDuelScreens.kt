package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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

@Composable
fun BotDuelSetupScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    val currentProfileState = viewModel.profileState.collectAsState()
    val profile = currentProfileState.value ?: com.example.data.StudentProfile()

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Create Arithmetic Bot Match ⚔️",
                onBack = onBack
            )
        },
        containerColor = ColorBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Introductory Card
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorGreenContainer),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 40.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "Kidmath Bot Duel Mode! 🏆",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorHeading
                        )
                        Text(
                            text = "Fill your Quest Cart with math questions and pick an iconic opponent to test your speed!",
                            fontSize = 13.sp,
                            color = ColorText
                        )
                    }
                }
            }

            // 1. QUEST CART: Select Math Operations
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ColorCreamBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🛒 Step 1: Quest Cart (Operations)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                    Text(
                        text = "Choose the types of equations to put in your battle bucket!",
                        fontSize = 12.sp,
                        color = ColorText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val activeOpsCount = listOf(
                        viewModel.duelOpAddition,
                        viewModel.duelOpSubtraction,
                        viewModel.duelOpMultiplication,
                        viewModel.duelOpDivision
                    ).count { it }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OperationToggleChip(
                            label = "Addition ➕",
                            isChecked = viewModel.duelOpAddition,
                            onToggle = {
                                if (activeOpsCount > 1 || !viewModel.duelOpAddition) {
                                    viewModel.duelOpAddition = !viewModel.duelOpAddition
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("select_addition_duel")
                        )
                        OperationToggleChip(
                            label = "Subtraction ➖",
                            isChecked = viewModel.duelOpSubtraction,
                            onToggle = {
                                if (activeOpsCount > 1 || !viewModel.duelOpSubtraction) {
                                    viewModel.duelOpSubtraction = !viewModel.duelOpSubtraction
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("select_subtraction_duel")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OperationToggleChip(
                            label = "Multiplying ✖️",
                            isChecked = viewModel.duelOpMultiplication,
                            onToggle = {
                                if (activeOpsCount > 1 || !viewModel.duelOpMultiplication) {
                                    viewModel.duelOpMultiplication = !viewModel.duelOpMultiplication
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("select_multiplication_duel")
                        )
                        OperationToggleChip(
                            label = "Division ➗",
                            isChecked = viewModel.duelOpDivision,
                            onToggle = {
                                if (activeOpsCount > 1 || !viewModel.duelOpDivision) {
                                    viewModel.duelOpDivision = !viewModel.duelOpDivision
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("select_division_duel")
                        )
                    }
                }
            }

            // 2. RANGE AND TIMER
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ColorCreamBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "⚙️ Step 2: Numbers & Time Limit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )

                    // Target Ranges Quick Picks
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Practice Number Limits:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                        val limitPresets = listOf(10, 20, 50, 100)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            limitPresets.forEach { limit ->
                                val isSelected = viewModel.duelRangeMax == limit && viewModel.duelRangeMin == 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ColorGreenPrimary else ColorTealBg)
                                        .clickable {
                                            viewModel.duelRangeMin = 0
                                            viewModel.duelRangeMax = limit
                                        }
                                        .border(1.dp, if (isSelected) ColorGreenPrimary else ColorTealBorder, RoundedCornerShape(12.dp))
                                        .padding(vertical = 10.dp)
                                        .testTag("duel_limit_$limit"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "0–$limit",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else ColorHeading
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = ColorCreamBorder.copy(alpha = 0.5f))

                    // Timer selection (each match will have time 5-15mins, plus shorter defaults for snappiness)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select Match Duration Timer:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                        
                        // Select between presets: 1min (60s), 2min (120s), 5min (300s), 10min (600s), 15min (900s)
                        val durationPresets = listOf(
                            60 to "1 Min ⚡",
                            120 to "2 Min ⏱️",
                            300 to "5 Min 🏆",
                            600 to "10 Min 👑",
                            900 to "15 Min 🦁"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            durationPresets.take(3).forEach { (seconds, label) ->
                                val isSelected = viewModel.duelDurationSeconds == seconds
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFFF57C00) else ColorTealBg)
                                        .clickable { viewModel.duelDurationSeconds = seconds }
                                        .border(1.dp, if (isSelected) Color(0xFFE65100) else ColorTealBorder, RoundedCornerShape(12.dp))
                                        .padding(vertical = 10.dp)
                                        .testTag("duel_time_$seconds"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else ColorHeading
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            durationPresets.drop(3).forEach { (seconds, label) ->
                                val isSelected = viewModel.duelDurationSeconds == seconds
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFFF57C00) else ColorTealBg)
                                        .clickable { viewModel.duelDurationSeconds = seconds }
                                        .border(1.dp, if (isSelected) Color(0xFFE65100) else ColorTealBorder, RoundedCornerShape(12.dp))
                                        .padding(vertical = 10.dp)
                                        .testTag("duel_time_$seconds"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else ColorHeading
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. CHOOSE BOT OPPONENT
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, ColorCreamBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "👾 Step 3: Choose Your Opponent Bot",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                    Text(
                        text = "Different opponents solve math at different speeds and accuracy levels!",
                        fontSize = 12.sp,
                        color = ColorText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        items(viewModel.botCharacters) { bot ->
                            val isSelected = viewModel.selectedBot.name == bot.name
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ColorGreenContainer else ColorBg
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    3.dp,
                                    if (isSelected) ColorGreenPrimary else ColorCreamBorder
                                ),
                                modifier = Modifier
                                    .width(130.dp)
                                    .clickable { viewModel.selectBotOpponent(bot) }
                                    .testTag("bot_card_${bot.name.replace(" ", "_")}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = bot.emoji, fontSize = 28.sp)
                                    Text(
                                        text = bot.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ColorHeading,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (bot.level) {
                                                    "Easy" -> Color(0xFFC8E6C9)
                                                    "Medium" -> Color(0xFFFFE0B2)
                                                    else -> Color(0xFFD1C4E9)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = bot.level,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (bot.level) {
                                                "Easy" -> Color(0xFF2E7D32)
                                                "Medium" -> Color(0xFFE65100)
                                                else -> Color(0xFF4527A0)
                                            }
                                        )
                                    }
                                    
                                    Text(
                                        text = "${(bot.accuracy * 100).toInt()}% Correct\n⏰ ~${bot.delayRange.first / 1000}s Speed",
                                        fontSize = 10.sp,
                                        color = ColorText.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Start Duel
            Button(
                onClick = {
                    viewModel.startDuelMatch()
                    viewModel.navigateTo(Screen.LessonBotDuelPlay)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("start_bot_duel_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "START THE DUEL! ⚔️🎒",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OperationToggleChip(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isChecked) ColorGreenContainer else ColorBg)
            .clickable { onToggle() }
            .border(
                2.dp,
                if (isChecked) ColorGreenPrimary else ColorCreamBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = ColorGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isChecked) ColorHeading else ColorText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotDuelPlayScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentQuestion = viewModel.duelCurrentQuestion
    val studentProfileState = viewModel.profileState.collectAsState()
    val profile = studentProfileState.value ?: com.example.data.StudentProfile()

    // Screen content
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Duelling with ${viewModel.selectedBot.name}! 👾",
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.exitDuelMatch() },
                        modifier = Modifier.testTag("exit_duel_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Match",
                            tint = ColorHeading
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorCreamSurface)
            )
        },
        containerColor = ColorBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Section 1: Dashboard and Dynamic Progress Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                    border = BorderStroke(1.dp, ColorCreamBorder),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Timer Clock Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (viewModel.duelTimerRemaining <= 10) Color(0xFFFFCDD2) else ColorTealBg
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⏱️ Time Left: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorHeading
                            )
                            val minutes = viewModel.duelTimerRemaining / 60
                            val seconds = viewModel.duelTimerRemaining % 60
                            val timeStr = String.format("%02d:%02d", minutes, seconds)
                            Text(
                                text = timeStr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (viewModel.duelTimerRemaining <= 10) Color.Red else ColorHeading,
                                modifier = Modifier.testTag("duel_countdown_timer")
                            )
                        }

                        // Tug of War Score Line Progress Bar
                        val userScore = viewModel.duelUserScore
                        val botScore = viewModel.duelBotScore
                        val combined = (userScore + botScore).coerceAtLeast(1)
                        val fraction = userScore.toFloat() / combined

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Player
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = profile.avatar, fontSize = 24.sp)
                                Column {
                                    Text(text = "You", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                                    Text(text = "$userScore Solved", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorGreenPrimary)
                                }
                            }

                            // Bot
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = viewModel.selectedBot.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                                    Text(text = "$botScore Solved", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF57C00))
                                }
                                Text(text = viewModel.selectedBot.emoji, fontSize = 24.sp)
                            }
                        }

                        // Beautiful interactive linear progress track
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .testTag("duel_tug_of_war_progress"),
                            color = ColorGreenPrimary,
                            trackColor = Color(0xFFFFB74D) // Bot track color
                        )

                        // Status dynamic text
                        val scoreGap = userScore - botScore
                        val motivationStr = when {
                            scoreGap > 0 -> "You are winning by $scoreGap! Awesome! 🏆"
                            scoreGap == 0 -> "Perfect tie! Solve faster to pull ahead! 🔥"
                            else -> "${viewModel.selectedBot.name} is leading by ${-scoreGap}! You can do it! 💪"
                        }
                        Text(
                            text = motivationStr,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorHeading,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Section 2: Split Correct/Wrong indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // User Score/Ticks History
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                        modifier = Modifier.weight(1f).height(90.dp).border(1.dp, ColorCreamBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Your Track 🎒", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorText)
                            
                            // History circles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (viewModel.duelUserRecent.isEmpty()) {
                                    Text("Waiting...", fontSize = 11.sp, color = ColorText.copy(0.6f))
                                } else {
                                    viewModel.duelUserRecent.forEach { answer ->
                                        Text(
                                            text = if (answer) "✅" else "❌",
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bot Score/Ticks History plus status log
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                        modifier = Modifier.weight(1f).height(90.dp).border(1.dp, ColorCreamBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${viewModel.selectedBot.name} Track 👾", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorText)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (viewModel.duelBotRecent.isEmpty()) {
                                    Text("Thinking...", fontSize = 11.sp, color = ColorText.copy(0.6f))
                                } else {
                                    viewModel.duelBotRecent.forEach { answer ->
                                        Text(
                                            text = if (answer) "✅" else "❌",
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // AI status banner log
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorTealBg)
                        .border(1.dp, ColorTealBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.duelBotStatusText.ifEmpty { "🤔 ${viewModel.selectedBot.name} is waiting for the countdown..." },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("duel_bot_status_ticker")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section 3: The Main Question Card
                if (currentQuestion != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ColorCreamSurface),
                        border = BorderStroke(2.dp, ColorCreamBorder),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().weight(1.2f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Subtitle of operational emojis
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentQuestion.emojiCountVal1,
                                    fontSize = 28.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentQuestion.op) {
                                        "+" -> "➕"
                                        "-" -> "➖"
                                        "*" -> "✖️"
                                        else -> "➗"
                                    },
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentQuestion.emojiCountVal2,
                                    fontSize = 28.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display Equation Formula
                            Row(
                                modifier = Modifier.fillMaxWidth().testTag("duel_question_text"),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentQuestion.val1} ${currentQuestion.op} ${currentQuestion.val2} = ",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ColorHeading
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(ColorBg)
                                        .border(2.dp, ColorCreamBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (viewModel.duelUserSelectedOption != null) "${currentQuestion.correctAnswer}" else "❓",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (viewModel.duelUserSelectedOption != null) ColorGreenPrimary else Color(0xFFFF9800)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Section 4: Grid of 4 answer options
                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        val items = currentQuestion.options
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(items) { opt ->
                                val isAnswered = viewModel.duelUserSelectedOption != null
                                val isCorrectOption = opt == currentQuestion.correctAnswer
                                val isChosenByPlayer = opt == viewModel.duelUserSelectedOption
                                
                                val buttonColor = when {
                                    isAnswered && isCorrectOption -> Color(0xFFC8E6C9) // Green correct
                                    isAnswered && isChosenByPlayer && !isCorrectOption -> Color(0xFFFFCDD2) // Red incorrect selection
                                    else -> ColorCreamSurface
                                }
                                val borderColor = when {
                                    isAnswered && isCorrectOption -> ColorGreenPrimary
                                    isAnswered && isChosenByPlayer && !isCorrectOption -> Color.Red
                                    else -> ColorCreamBorder
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = buttonColor),
                                    border = BorderStroke(2.dp, borderColor),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(58.dp)
                                        .clickable(!isAnswered) { viewModel.submitDuelOption(opt) }
                                        .testTag("duel_option_$opt")
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$opt",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorHeading
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 5: DUEL FINISHED MODAL CARD OVERLAY
            if (viewModel.isDuelFinished) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ColorBg),
                        border = BorderStroke(3.dp, ColorGreenPrimary),
                        shape = RoundedCornerShape(26.dp),
                        modifier = Modifier.fillMaxWidth().testTag("duel_finished_dialog")
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val didUserWin = viewModel.duelUserScore > viewModel.duelBotScore
                            val isDraw = viewModel.duelUserScore == viewModel.duelBotScore

                            Text(
                                text = when {
                                    didUserWin -> "VICTORY! 🏆👑"
                                    isDraw -> "WHAT A MATCH! 🤝"
                                    else -> "GREAT EFFORT! 🌟"
                                },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (didUserWin) ColorGreenPrimary else Color(0xFFF57C00)
                            )

                            Text(
                                text = when {
                                    didUserWin -> "Wow! You defeated ${viewModel.selectedBot.emoji} ${viewModel.selectedBot.name} in an intense mathematical duel!"
                                    isDraw -> "A perfect double-draw! You solved equal equations alongside the math champion ${viewModel.selectedBot.emoji} ${viewModel.selectedBot.name}!"
                                    else -> "You fought well! ${viewModel.selectedBot.emoji} ${viewModel.selectedBot.name} won this round, but you are getting smarter every second!"
                                },
                                fontSize = 14.sp,
                                color = ColorText,
                                textAlign = TextAlign.Center
                            )

                            // Scores Comparison Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ColorCreamSurface)
                                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "You", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${viewModel.duelUserScore}",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ColorGreenPrimary
                                        )
                                    }
                                    
                                    Text(
                                        text = "VS",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ColorHeading.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(horizontal = 30.dp)
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = viewModel.selectedBot.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${viewModel.duelBotScore}",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFF57C00)
                                        )
                                    }
                                }
                            }

                            // Coin payout display
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🪙 Payout Winner Reward: +${viewModel.duelCoinsAwarded} Coins!",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Return & Rematch Actions
                            Button(
                                onClick = {
                                    viewModel.startDuelMatch() // Rematch!
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorGreenPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("rematch_finished_action")
                            ) {
                                Text("Play Rematch ⚔️", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.exitDuelMatch()
                                },
                                border = BorderStroke(2.dp, ColorCreamBorder),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("exit_finished_action")
                            ) {
                                Text("Back to Dashboard", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                            }
                        }
                    }
                }
            }
        }
    }
}

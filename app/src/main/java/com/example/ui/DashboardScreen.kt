package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.StudentProfile
import com.example.ui.theme.*

// Playful pastel gradients for standard fallbacks if needed
val PurpleGradient = Brush.horizontalGradient(listOf(Color(0xFFBA68C8), Color(0xFF9575CD)))
val GreenGradient = Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFF4DB6AC)))
val CoralGradient = Brush.horizontalGradient(listOf(Color(0xFFFF8A65), Color(0xFFFFB74D)))
val BlueGradient = Brush.horizontalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF64B5F6)))
val PinkGradient = Brush.horizontalGradient(listOf(Color(0xFFF06292), Color(0xFFFF8A80)))
val RedGradient = Brush.horizontalGradient(listOf(Color(0xFFE57373), Color(0xFFEF5350)))

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    profile: StudentProfile,
    onNavigate: (Screen) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // PROFILE COLOURED HEADER CARD (Naturally Toned)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_profile_card"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, ColorCreamBorder),
                    colors = CardDefaults.cardColors(containerColor = ColorCreamSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .background(ColorCreamSurface)
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Selected Avatar bubble - Green container matching profile fox in design HTML
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(76.dp)
                                    .background(ColorGreenContainer, CircleShape)
                                    .border(2.dp, ColorGreenPrimary, CircleShape)
                                    .clickable { showEditDialog = true }
                                    .testTag("avatar_indicator_button")
                            ) {
                                Text(
                                    text = profile.avatar,
                                    fontSize = 44.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = profile.name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorHeading
                                    )
                                    IconButton(
                                        onClick = { showEditDialog = true },
                                        modifier = Modifier.size(24.dp).testTag("edit_profile_icon")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Profile name",
                                            tint = ColorGreenPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                // Badge level subtitle styled like the design spec "Level 8 • Rookie Hero"
                                Text(
                                    text = "Level ${profile.additionLevel + profile.wordMatchingLevel} • Rookie Hero",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorGreenPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Streak and Coins Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Streak Card
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ColorBg)
                                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🔥", fontSize = 18.sp)
                                Text(
                                    text = "${profile.streak} Days",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ColorHeading
                                )
                            }

                            // Coins Card
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ColorBg)
                                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🪙", fontSize = 18.sp)
                                Text(
                                    text = "${profile.coins} Coins",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ColorHeading
                                )
                            }
                        }
                    }
                }
            }

            // DAILY GOAL CARD (Extracted from the Design HTML Progress Dashboard Card)
            item {
                val currentXp = (profile.coins % 20) * 15 + 150
                val maxXp = 300
                val progressFraction = currentXp.toFloat() / maxXp.toFloat()
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_goal_card"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, ColorTealBorder),
                    colors = CardDefaults.cardColors(containerColor = ColorTealBg)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Daily Goal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A635F)
                            )
                            Text(
                                text = "$currentXp / $maxXp XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4A635F)
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = { progressFraction.coerceIn(0.1f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = ColorTealProgress,
                            trackColor = ColorTealTrack
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(ColorTealProgress, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("★", color = Color.White, fontSize = 12.sp)
                            }
                            Text(
                                text = "You are doing great! Complete more lessons to earn stars and unlock toys!",
                                fontSize = 12.sp,
                                color = Color(0xFF5D7A75)
                            )
                        }
                    }
                }
            }

            // STICKER / TROPHY CABINET
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Sticker & Toy Box 🧸",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorHeading
                        )
                        Button(
                            onClick = { onNavigate(Screen.ToyShop) },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Shop ✨", fontSize = 12.sp, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (profile.unlockedToys.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ColorCreamBorder),
                            colors = CardDefaults.cardColors(containerColor = ColorCreamSurface)
                        ) {
                            Box(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Your toy box is empty! Spend coins in the Toy Shop to collect fun toys! 🎈",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = ColorText.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        val toys = profile.unlockedToys.split(",")
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(toys) { toy ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(ColorBg, CircleShape)
                                        .border(1.dp, ColorCreamBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = toy, fontSize = 34.sp)
                                }
                            }
                        }
                    }
                }
            }

            // HERO QUESTS: MATH SECTIONS
            item {
                Text(
                    text = stringResource(R.string.lessons_header),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // High impact card banner for custom Bot Duel Match
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, Color(0xFFFFB74D)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("bot_duel_dashboard_banner")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, Color(0xFFFFCC80), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👾", fontSize = 32.sp)
                            }
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFE0B2))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "NEW BATTLE ARENA 🎮",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Text(
                                    text = "AI Character Bot Duel! ⚔️",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ColorHeading
                                )
                            }
                        }

                        Text(
                            text = "Fill your quest cart with addition, subtraction, multiplication, or division queries! Choose legendary bots like Elsa ❄️, Pikachu ⚡ or Kuromi 😈 and race against their live accuracy score clock!",
                            fontSize = 13.sp,
                            color = ColorText,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { onNavigate(Screen.LessonBotDuelSetup) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("enter_bot_duel_arena_btn")
                        ) {
                            Text(
                                text = "Enter Battle Arena 🚀",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Map layout items representing math topics (styled using Natural Tones)
            item {
                LessonCard(
                    title = stringResource(R.string.addition_label),
                    subtitle = "Join number blocks!",
                    levelStr = "Level ${profile.additionLevel}",
                    progressFraction = profile.additionLevel / 10f,
                    icon = "➕",
                    backgroundColor = Color(0xFFF9E8E8),
                    borderColor = Color(0xFFE9D1D1),
                    textColor = Color(0xFF6D4242),
                    subtextColor = Color(0xFF8E5E5E),
                    progressColor = Color(0xFFE57373),
                    progressTrackColor = Color(0xFFFFCDD2),
                    tag = "math_addition_lesson_button"
                ) {
                    viewModel.selectMathOpAndNavigate("+")
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.subtraction_label),
                    subtitle = "Pop bubbles to subtract!",
                    levelStr = "Level ${profile.subtractionLevel}",
                    progressFraction = profile.subtractionLevel / 10f,
                    icon = "➖",
                    backgroundColor = Color(0xFFF9E8E8),
                    borderColor = Color(0xFFE9D1D1),
                    textColor = Color(0xFF6D4242),
                    subtextColor = Color(0xFF8E5E5E),
                    progressColor = Color(0xFFEF5350),
                    progressTrackColor = Color(0xFFFFCDD2),
                    tag = "math_subtraction_lesson_button"
                ) {
                    viewModel.selectMathOpAndNavigate("-")
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.multiplication_label),
                    subtitle = "Grid puzzle multiplications!",
                    levelStr = "Level ${profile.multiplicationLevel}",
                    progressFraction = profile.multiplicationLevel / 10f,
                    icon = "✖️",
                    backgroundColor = Color(0xFFFFF4E5),
                    borderColor = Color(0xFFF6E2C9),
                    textColor = Color(0xFF7E5D3E),
                    subtextColor = Color(0xFF9A7D5E),
                    progressColor = Color(0xFFFFB74D),
                    progressTrackColor = Color(0xFFFFE0B2),
                    tag = "math_multiplication_lesson_button"
                ) {
                    viewModel.selectMathOpAndNavigate("*")
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.division_label),
                    subtitle = "Equally share delicious treats!",
                    levelStr = "Level ${profile.divisionLevel}",
                    progressFraction = profile.divisionLevel / 10f,
                    icon = "➗",
                    backgroundColor = Color(0xFFFFF4E5),
                    borderColor = Color(0xFFF6E2C9),
                    textColor = Color(0xFF7E5D3E),
                    subtextColor = Color(0xFF9A7D5E),
                    progressColor = Color(0xFFFFA726),
                    progressTrackColor = Color(0xFFFFE0B2),
                    tag = "math_division_lesson_button"
                ) {
                    viewModel.selectMathOpAndNavigate("/")
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.missing_number_label),
                    subtitle = "Find the missing piece!",
                    levelStr = "Level ${profile.missingNumberLevel}",
                    progressFraction = profile.missingNumberLevel / 10f,
                    icon = "❓",
                    backgroundColor = Color(0xFFFFF4E5),
                    borderColor = Color(0xFFF6E2C9),
                    textColor = Color(0xFF7E5D3E),
                    subtextColor = Color(0xFF9A7D5E),
                    progressColor = Color(0xFFFFB74D),
                    progressTrackColor = Color(0xFFFFE0B2),
                    tag = "missing_number_lesson_button"
                ) {
                    onNavigate(Screen.LessonMissingNum)
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.sudoku_label),
                    subtitle = "Train your big brain!",
                    levelStr = "Level ${profile.sudokuLevel}",
                    progressFraction = profile.sudokuLevel / 10f,
                    icon = "🧩",
                    backgroundColor = Color(0xFFF4F9E8),
                    borderColor = Color(0xFFE3E9D1),
                    textColor = Color(0xFF566D42),
                    subtextColor = Color(0xFF6D8E5E),
                    progressColor = Color(0xFF8BA861),
                    progressTrackColor = Color(0xFFE0EBCF),
                    tag = "sudoku_lesson_button"
                ) {
                    onNavigate(Screen.LessonSudoku)
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.cube_counting_label),
                    subtitle = "Count cubes in 3D towers!",
                    levelStr = "Level ${profile.cubeCountingLevel}",
                    progressFraction = profile.cubeCountingLevel / 10f,
                    icon = "📦",
                    backgroundColor = Color(0xFFF5EEFD),
                    borderColor = Color(0xFFE6D6FA),
                    textColor = Color(0xFF5A4475),
                    subtextColor = Color(0xFF755D93),
                    progressColor = Color(0xFF9B6FE6),
                    progressTrackColor = Color(0xFFE6DDFC),
                    tag = "cube_counting_lesson_button"
                ) {
                    onNavigate(Screen.LessonCubeCounting)
                }
            }

            // EMOJI / WORLD WORD PLAY
            item {
                Text(
                    text = "Reading & Spelling Quests 📚",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item {
                LessonCard(
                    title = stringResource(R.string.word_match_label),
                    subtitle = "Match words with picture cards!",
                    levelStr = "Level ${profile.wordMatchingLevel}",
                    progressFraction = profile.wordMatchingLevel / 10f,
                    icon = "cat",
                    backgroundColor = Color(0xFFE8F1F9),
                    borderColor = Color(0xFFD1DFE9),
                    textColor = Color(0xFF42506D),
                    subtextColor = Color(0xFF5E6D8E),
                    progressColor = Color(0xFF64B5F6),
                    progressTrackColor = Color(0xFFBBDEFB),
                    tag = "word_match_lesson_button"
                ) {
                    onNavigate(Screen.LessonWordMatch)
                }
            }

            item {
                LessonCard(
                    title = stringResource(R.string.spelling_label),
                    subtitle = "Arrange letters to spell words!",
                    levelStr = "Level ${profile.spellingLevel}",
                    progressFraction = profile.spellingLevel / 10f,
                    icon = "📖",
                    backgroundColor = Color(0xFFE8F1F9),
                    borderColor = Color(0xFFD1DFE9),
                    textColor = Color(0xFF42506D),
                    subtextColor = Color(0xFF5E6D8E),
                    progressColor = Color(0xFFF06292),
                    progressTrackColor = Color(0xFFF8BBD0),
                    tag = "spelling_lesson_button"
                ) {
                    onNavigate(Screen.LessonSpelling)
                }
            }

            // ACTIVE PLAYROOM MINIGAMES
            item {
                Text(
                    text = "Active Playroom Minigames 🎈",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.MathMinigame) }
                        .testTag("math_minigame_button"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, ColorGreenPrimary),
                    colors = CardDefaults.cardColors(containerColor = ColorGreenPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Text("🎮", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Math Run: Jungle Quest",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Win rare stickers and coins today!",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text("❯", color = Color.White, fontSize = 20.sp)
                    }
                }
            }

            // ACHIEVED STREAK BADGES CABINET
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    Text(
                        text = "My Achieved Badges 🎖️",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val badges = profile.unlockedBadges.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        badges.forEach { badge ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ColorCreamSurface)
                                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(ColorGreenContainer, CircleShape)
                                ) {
                                    Text("🥇", fontSize = 18.sp)
                                }
                                Text(
                                    text = badge,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorHeading
                                )
                            }
                        }
                    }
                }
            }
        }

        // EDIT PROFILE / INJECT NICKNAME OVERLAY DIALOG
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("What is your Hero name? 🦁", color = ColorHeading) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        var tempName by remember { mutableStateOf(profile.name) }
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Your Hero Name") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_name_text_input")
                        )
                        
                        Text("Select your current Avatar icon:", fontWeight = FontWeight.SemiBold, color = ColorText)
                        val avList = profile.unlockedAvatars.split(",").map { it.trim() }
                        
                        // Scrollable list of unlocked avatars
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(avList) { emoji ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            if (profile.avatar == emoji) ColorGreenPrimary else ColorCreamSurface,
                                            CircleShape
                                        )
                                        .border(
                                            1.dp,
                                            if (profile.avatar == emoji) ColorGreenPrimary else ColorCreamBorder,
                                            CircleShape
                                        )
                                        .clickable {
                                            viewModel.changeAvatar(emoji)
                                        }
                                        .testTag("avatar_select_$emoji")
                                ) {
                                    Text(emoji, fontSize = 30.sp)
                                }
                            }
                        }

                        // Call to action button to submit
                        Button(
                            onClick = {
                                viewModel.updateNickname(tempName)
                                showEditDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_profile_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorGreenPrimary)
                        ) {
                            Text("Save My Hero", color = Color.White)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel", color = ColorText)
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun LessonCard(
    title: String,
    subtitle: String,
    levelStr: String,
    progressFraction: Float,
    icon: String,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    subtextColor: Color,
    progressColor: Color,
    progressTrackColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Box(
                        modifier = Modifier
                            .background(textColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(vertical = 2.dp, horizontal = 6.dp)
                    ) {
                        Text(
                            text = levelStr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = textColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = subtextColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Progress slider bar mimicking level achievements
                LinearProgressIndicator(
                    progress = { progressFraction.coerceIn(0.1f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = progressTrackColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .background(textColor.copy(alpha = 0.1f), CircleShape)
            ) {
                Text(text = icon, fontSize = 28.sp)
            }
        }
    }
}

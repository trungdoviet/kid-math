package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WordMatchingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val round = viewModel.matchRound
    if (round == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ColorGreenPrimary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Emoji Word Match Quest 🐈",
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
                    text = "Tap a WORD on the left, then tap its MATCHING PICTURE on the right! 🍎",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Left words and right emojis panels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT COLUMN (Words)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        round.leftWords.forEach { word ->
                            // Find matching status in round pairs
                            val pairMatch = round.pairs.find { it.word == word }
                            val isMatched = pairMatch?.isMatched == true
                            val isSelected = viewModel.selectedLeftWord == word

                            val cardColor = when {
                                isMatched -> ColorGreenContainer
                                isSelected -> ColorTealBg
                                else -> ColorCreamSurface
                            }
                            
                            val borderColor = when {
                                isMatched -> ColorGreenPrimary
                                isSelected -> ColorTealProgress
                                else -> ColorCreamBorder
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .shadow(if (isMatched) 0.dp else 1.dp, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(enabled = !isMatched) {
                                        viewModel.selectLeftWordCard(word)
                                    }
                                    .testTag("left_word_$word"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, borderColor),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = word,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMatched) ColorHeading else ColorText
                                    )
                                    if (isMatched) {
                                        Text(
                                            text = "✅",
                                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // RIGHT COLUMN (Emojis)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        round.rightEmojis.forEach { emoji ->
                            val pairMatch = round.pairs.find { it.emoji == emoji }
                            val isMatched = pairMatch?.isMatched == true
                            val isSelected = viewModel.selectedRightEmoji == emoji

                            val cardColor = when {
                                isMatched -> ColorGreenContainer
                                isSelected -> ColorTealBg
                                else -> ColorCreamSurface
                            }
                            
                            val borderColor = when {
                                isMatched -> ColorGreenPrimary
                                isSelected -> ColorTealProgress
                                else -> ColorCreamBorder
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .shadow(if (isMatched) 0.dp else 1.dp, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(enabled = !isMatched) {
                                        viewModel.selectRightEmojiCard(emoji)
                                    }
                                    .testTag("right_emoji_$emoji"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, borderColor),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 30.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    if (isMatched) {
                                        Text(
                                            text = "✅",
                                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SCORE HUD
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(24.dp))
                    .background(ColorCreamSurface, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Matched ${viewModel.matchScoreCount} of 4 🌟",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorHeading
                    )
                }

                if (viewModel.matchScoreCount == 4) {
                    Text(
                        text = "Terrific Word Matcher! 🎉",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ColorGreenPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun SpellingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val puzzle = viewModel.spellingPuzzleState
    if (puzzle == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ColorGreenPrimary)
        }
        return
    }

    val answers = puzzle.currentAnswerList
    val targetLength = puzzle.targetWord.length
    val lettersRemaining = puzzle.jumbledLetters

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Magic Word Speller 🐝",
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
            // Stats Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Puzzle ${viewModel.spellingQuizIndex + 1} of ${viewModel.totalSpellingQuizzes}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorText
                )
                Text(
                    text = "🏆 Stars: ${viewModel.spellingScore}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorGreenPrimary
                )
            }

            // TARGET HINT PICTURE CARD (The visual cue)
            Card(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(1.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ColorCreamBorder),
                colors = CardDefaults.cardColors(containerColor = ColorCreamSurface)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = puzzle.targetEmoji,
                            fontSize = 72.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "How do you spell this?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorText.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // CURRENT ASSEMBLED ANSWER (Empty slot boxes matching target letters)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, ColorCreamBorder, RoundedCornerShape(16.dp))
                    .background(ColorCreamSurface, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                for (i in 0 until targetLength) {
                    val letter = if (i < answers.size) answers[i] else ""
                    val isFilled = letter.isNotEmpty()

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFilled) ColorGreenPrimary else ColorBg)
                            .border(1.dp, if (isFilled) ColorGreenPrimary else ColorCreamBorder, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = letter,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFilled) Color.White else ColorText
                        )
                    }
                }

                // Append Reset Key icon to clear assembled inputs
                IconButton(
                    onClick = { viewModel.clearSpellingAnswer() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, ColorCreamBorder, CircleShape)
                        .background(ColorBg)
                        .testTag("spelling_clear_button")
                ) {
                    Text("✖️", fontSize = 16.sp, color = ColorHeading)
                }
            }

            // SCRAMBLED ALPHABET BUTTONS DRAWER
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tap letters in order to spell the word! 👇",
                    fontSize = 14.sp,
                    color = ColorHeading,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Layout jumbled characters in scrollable or standard row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    lettersRemaining.forEachIndexed { idx, letter ->
                        // Simple occurrence based highlight or lock helper
                        // Allow infinite clicking since we provide a handy clear button
                        Button(
                            onClick = { viewModel.tapSpellingLetter(letter) },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("spelling_key_${letter}_$idx"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = letter,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

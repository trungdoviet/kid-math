package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.StudentProfile
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun BubblePopMinigameScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val activeState = viewModel.bubbleGameActive
    val timer = viewModel.bubbleTimerSeconds
    val score = viewModel.bubbleScore
    val targetVal = viewModel.bubbleTargetVal
    val bubbles = viewModel.bubbleList

    // Automatic restart triggers
    LaunchedEffect(Unit) {
        viewModel.startBubblePopMinigame()
    }

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Math Bubble Pop! 🫧",
                onBack = onBack
            )
        },
        containerColor = ColorBg
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ColorBg)
        ) {
            val areaWidth = maxWidth
            val areaHeight = maxHeight

            if (activeState) {
                // GAME IN RUNNING STATE
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // TIMER & TARGET HUD (Naturally Toned)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ColorCreamBorder, RoundedCornerShape(24.dp))
                            .background(ColorCreamSurface, RoundedCornerShape(24.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "POP FORMULAS EQUAL TO: 🎯",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ColorGreenPrimary)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$targetVal",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "⏳ $timer Secs",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (timer <= 5) Color(0xFFEF5350) else ColorHeading
                            )
                            Text(
                                text = "Score: $score 🎈",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorText
                            )
                        }
                    }

                    // CENTRAL INTERACTIVE FLOATING BOX CHASSIS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        bubbles.forEach { bubble ->
                            if (!bubble.isPopped) {
                                val parseColor = try {
                                    // Make bubble colors slightly pastel toned
                                    Color(android.graphics.Color.parseColor(bubble.questionHex))
                                } catch (e: Exception) {
                                    ColorTealProgress
                                }

                                // Render floatable bubble elements
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .offset(
                                            x = areaWidth * bubble.xOffsetFraction,
                                            y = areaHeight * bubble.yPosition
                                        )
                                        .shadow(2.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(parseColor.copy(alpha = 0.85f))
                                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                                        .clickable {
                                            viewModel.popBubble(bubble)
                                        }
                                        .testTag("bubble_${bubble.id}")
                                ) {
                                    Text(
                                        text = bubble.formula,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Tip: Choose matching sums to win Golden Coins!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // GAME OVER AND RECONCILIATION REPORT CARD
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .width(320.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, ColorCreamBorder),
                        colors = CardDefaults.cardColors(containerColor = ColorCreamSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Bubble Time Up! 🫧🔔",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ColorHeading,
                                textAlign = TextAlign.Center
                            )
                            
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(ColorGreenContainer, CircleShape)
                                    .border(2.dp, ColorGreenPrimary, CircleShape)
                            ) {
                                Text("🏆", fontSize = 54.sp)
                            }

                            Text(
                                text = "You popped $score correct formulas!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorHeading,
                                textAlign = TextAlign.Center
                            )

                            // Explain coin payouts
                            val coinBonus = score * 2
                            Card(
                                border = BorderStroke(1.dp, ColorTealBorder),
                                colors = CardDefaults.cardColors(containerColor = ColorTealBg),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🪙", fontSize = 24.sp)
                                    Text(
                                        text = "Payout Reward: +$coinBonus Coins!",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = ColorHeading
                                    )
                                }
                            }

                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = ColorGreenPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("back_to_dashboard")
                            ) {
                                Text("Back to Dashboard 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===========================================
// TOY SHOP & CUSTOMIZABLE AVATAR SHOP (Natural Tones)
// ===========================================
@Composable
fun ToyShopScreen(
    viewModel: MainViewModel,
    profile: StudentProfile,
    onBack: () -> Unit
) {
    var shopFeedback by remember { mutableStateOf<String?>(null) }
    var shopErrorFeedback by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopBarHelper(
                title = "Toy Box & Sticker Shop 🧸✨",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Coin balance display (Styled as Green Container with border)
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, ColorGreenPrimary),
                colors = CardDefaults.cardColors(containerColor = ColorGreenContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("My Wallet", fontSize = 12.sp, color = ColorHeading, fontWeight = FontWeight.Bold)
                        Text("${profile.coins} Golden Coins 🪙", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Text("⭐", fontSize = 18.sp)
                    }
                }
            }

            // Messages row
            AnimatedVisibility(visible = shopFeedback != null || shopErrorFeedback != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (shopFeedback != null) ColorGreenContainer else Color(0xFFF9E8E8)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = shopFeedback ?: shopErrorFeedback ?: "",
                            fontWeight = FontWeight.Bold,
                            color = if (shopFeedback != null) ColorHeading else Color(0xFF6D4242),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // SCROLLABLE SECTIONS
            Column(modifier = Modifier.weight(1f)) {
                // Section tabs
                Text(
                    text = "Charismatic Avatars 🦁🐯🐨",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .height(115.dp)
                        .fillMaxWidth()
                ) {
                    val unlockedAvs = profile.unlockedAvatars.split(",").map { it.trim() }
                    
                    items(viewModel.shopAvatars) { av ->
                        val isOwned = unlockedAvs.contains(av.emoji) || av.defaultUnlocked
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isOwned) ColorGreenContainer else ColorCreamSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .border(
                                    1.dp,
                                    if (isOwned) ColorGreenPrimary else ColorCreamBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (isOwned) {
                                        viewModel.changeAvatar(av.emoji)
                                        shopFeedback = "Set avatar to ${av.emoji} successfully!"
                                        shopErrorFeedback = null
                                    } else {
                                        viewModel.buyAvatar(
                                            emoji = av.emoji,
                                            cost = av.price,
                                            onSuccess = {
                                                shopFeedback = "Successfully purchased avatar ${av.emoji}! 🎉"
                                                shopErrorFeedback = null
                                            },
                                            onError = { err ->
                                                shopErrorFeedback = err
                                                shopFeedback = null
                                            }
                                        )
                                    }
                                }
                                .testTag("shop_avatar_${av.emoji}")
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(av.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isOwned) "Owned" else "🪙${av.price}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOwned) ColorHeading else ColorHeading.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Classic Toy Sticker Chest 🧸🎈",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorHeading
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val unlockedStickers = profile.unlockedToys.split(",").map { it.trim() }

                    items(viewModel.shopToys) { toy ->
                        val isOwned = unlockedStickers.contains(toy.emoji)
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isOwned) ColorGreenContainer else ColorCreamSurface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(
                                    1.dp,
                                    if (isOwned) ColorGreenPrimary else ColorCreamBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (isOwned) {
                                        shopFeedback = "You already own this! It populates your sticker collection!"
                                        shopErrorFeedback = null
                                    } else {
                                        viewModel.buyToy(
                                            emoji = toy.emoji,
                                            cost = toy.price,
                                            onSuccess = {
                                                shopFeedback = "Bought ${toy.name}! Check your Toy Box!"
                                                shopErrorFeedback = null
                                            },
                                            onError = { err ->
                                                shopErrorFeedback = err
                                                shopFeedback = null
                                            }
                                        )
                                    }
                                }
                                .testTag("shop_toy_${toy.emoji}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(ColorBg, CircleShape)
                                        .border(1.dp, ColorCreamBorder, CircleShape)
                                ) {
                                    Text(toy.emoji, fontSize = 28.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(toy.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorHeading)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isOwned) "Unlocked" else "Price: 🪙${toy.price}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isOwned) ColorGreenPrimary else ColorHeading.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val mainViewModel: MainViewModel = viewModel()
                val profileOpt by mainViewModel.profileState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing // respecting device notches cleanly
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        val activeProfile = profileOpt
                        if (activeProfile == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(com.example.ui.theme.ColorBg),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = com.example.ui.theme.ColorGreenPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading your math universe... 🦁✨",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.ColorHeading,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Route cleanly to screens with smooth transitions
                            AnimatedContent(
                                targetState = mainViewModel.currentScreen,
                                label = "ScreenTransition",
                                transitionSpec = {
                                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                                }
                            ) { screen ->
                                when (screen) {
                                    Screen.Dashboard -> {
                                        DashboardScreen(
                                            viewModel = mainViewModel,
                                            profile = activeProfile,
                                            onNavigate = { target ->
                                                mainViewModel.navigateTo(target)
                                            }
                                        )
                                    }
                                    Screen.LessonAddition -> {
                                        ArithmeticQuizScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonSubtraction -> {
                                        ArithmeticQuizScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonMultiplication -> {
                                        ArithmeticQuizScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonDivision -> {
                                        ArithmeticQuizScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonMathSetup -> {
                                        MathSetupScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonMissingNum -> {
                                        MissingNumQuizScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonSudoku -> {
                                        SudokuScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonWordMatch -> {
                                        WordMatchingScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonSpelling -> {
                                        SpellingScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonCubeCounting -> {
                                        CubeCountingQuizScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.MathMinigame -> {
                                        BubblePopMinigameScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonBotDuelSetup -> {
                                        BotDuelSetupScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    Screen.LessonBotDuelPlay -> {
                                        BotDuelPlayScreen(
                                            viewModel = mainViewModel,
                                            onBack = { mainViewModel.exitDuelMatch() }
                                        )
                                    }
                                    Screen.ToyShop -> {
                                        ToyShopScreen(
                                            viewModel = mainViewModel,
                                            profile = activeProfile,
                                            onBack = { mainViewModel.navigateTo(Screen.Dashboard) }
                                        )
                                    }
                                    else -> {
                                        DashboardScreen(
                                            viewModel = mainViewModel,
                                            profile = activeProfile,
                                            onNavigate = { target ->
                                                mainViewModel.navigateTo(target)
                                            }
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
}

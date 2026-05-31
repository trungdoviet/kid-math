package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class StudentRepository(private val dao: StudentProfileDao) {
    val activeProfile: Flow<StudentProfile?> = dao.getProfile()

    suspend fun getOrCreateProfile(): StudentProfile {
        var profile = dao.getProfileSync()
        if (profile == null) {
            profile = StudentProfile()
            dao.insertProfile(profile)
        }
        return profile
    }

    suspend fun updateNickname(newName: String) {
        val profile = getOrCreateProfile()
        dao.updateProfile(profile.copy(name = newName))
    }

    suspend fun updateAvatar(newAvatar: String) {
        val profile = getOrCreateProfile()
        dao.updateProfile(profile.copy(avatar = newAvatar))
    }

    suspend fun addCoins(amount: Int) {
        val profile = getOrCreateProfile()
        val updatedCoins = profile.coins + amount
        dao.updateProfile(profile.copy(coins = updatedCoins))
    }

    suspend fun purchaseAvatar(avatarEmoji: String, cost: Int): Boolean {
        val profile = getOrCreateProfile()
        if (profile.coins >= cost && !profile.unlockedAvatars.contains(avatarEmoji)) {
            val updatedCoins = profile.coins - cost
            val updatedAvatars = "${profile.unlockedAvatars},$avatarEmoji"
            dao.updateProfile(profile.copy(
                coins = updatedCoins,
                unlockedAvatars = updatedAvatars
            ))
            return true
        }
        return false
    }

    suspend fun purchaseToy(toyEmoji: String, cost: Int): Boolean {
        val profile = getOrCreateProfile()
        if (profile.coins >= cost && !profile.unlockedToys.contains(toyEmoji)) {
            val updatedCoins = profile.coins - cost
            val updatedToys = if (profile.unlockedToys.isEmpty()) toyEmoji else "${profile.unlockedToys},$toyEmoji"
            dao.updateProfile(profile.copy(
                coins = updatedCoins,
                unlockedToys = updatedToys
            ))
            return true
        }
        return false
    }

    suspend fun updateLessonProgress(
        lessonType: String,
        correctAnswers: Int,
        totalQuestions: Int,
        solvedInc: Int
    ) {
        val profile = getOrCreateProfile()
        
        // Calculate new metrics
        val currentCoins = profile.coins
        val coinsEarned = correctAnswers * 1 + (if (correctAnswers == totalQuestions) 3 else 0) // perfect score bonus!
        val newCoins = currentCoins + coinsEarned

        // Check and update streak
        val currentTime = System.currentTimeMillis()
        val diffMs = currentTime - profile.lastActiveTime
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMs)
        val newStreak = when {
            diffDays == 1L -> profile.streak + 1
            diffDays > 1L -> 1 // reset streak
            else -> profile.streak // same day, preserve streak
        }

        // Build updated profile depending on lessonType
        var updatedProfile = profile.copy(
            coins = newCoins,
            streak = newStreak,
            lastActiveTime = currentTime
        )

        // Update counts and levels based on play
        when (lessonType) {
            "addition" -> {
                val newLevel = if (correctAnswers >= 4 && profile.additionLevel < 10) profile.additionLevel + 1 else profile.additionLevel
                updatedProfile = updatedProfile.copy(
                    additionLevel = newLevel,
                    totalMathSolved = profile.totalMathSolved + solvedInc
                )
            }
            "subtraction" -> {
                val newLevel = if (correctAnswers >= 4 && profile.subtractionLevel < 10) profile.subtractionLevel + 1 else profile.subtractionLevel
                updatedProfile = updatedProfile.copy(
                    subtractionLevel = newLevel,
                    totalMathSolved = profile.totalMathSolved + solvedInc
                )
            }
            "multiplication" -> {
                val newLevel = if (correctAnswers >= 4 && profile.multiplicationLevel < 10) profile.multiplicationLevel + 1 else profile.multiplicationLevel
                updatedProfile = updatedProfile.copy(
                    multiplicationLevel = newLevel,
                    totalMathSolved = profile.totalMathSolved + solvedInc
                )
            }
            "division" -> {
                val newLevel = if (correctAnswers >= 4 && profile.divisionLevel < 10) profile.divisionLevel + 1 else profile.divisionLevel
                updatedProfile = updatedProfile.copy(
                    divisionLevel = newLevel,
                    totalMathSolved = profile.totalMathSolved + solvedInc
                )
            }
            "missing" -> {
                val newLevel = if (correctAnswers >= 4 && profile.missingNumberLevel < 10) profile.missingNumberLevel + 1 else profile.missingNumberLevel
                updatedProfile = updatedProfile.copy(
                    missingNumberLevel = newLevel,
                    totalMathSolved = profile.totalMathSolved + solvedInc
                )
            }
            "sudoku" -> {
                val newLevel = if (correctAnswers >= 1 && profile.sudokuLevel < 10) profile.sudokuLevel + 1 else profile.sudokuLevel
                updatedProfile = updatedProfile.copy(
                    sudokuLevel = newLevel,
                    totalSudokuSolved = profile.totalSudokuSolved + 1
                )
            }
            "wordMatch" -> {
                val newLevel = if (correctAnswers >= 4 && profile.wordMatchingLevel < 10) profile.wordMatchingLevel + 1 else profile.wordMatchingLevel
                updatedProfile = updatedProfile.copy(
                    wordMatchingLevel = newLevel,
                    totalWordsSolved = profile.totalWordsSolved + solvedInc
                )
            }
            "spelling" -> {
                val newLevel = if (correctAnswers >= 4 && profile.spellingLevel < 10) profile.spellingLevel + 1 else profile.spellingLevel
                updatedProfile = updatedProfile.copy(
                    spellingLevel = newLevel,
                    totalWordsSolved = profile.totalWordsSolved + solvedInc
                )
            }
            "cubes" -> {
                val newLevel = if (correctAnswers >= 4 && profile.cubeCountingLevel < 10) profile.cubeCountingLevel + 1 else profile.cubeCountingLevel
                updatedProfile = updatedProfile.copy(
                    cubeCountingLevel = newLevel,
                    totalCubesSolved = profile.totalCubesSolved + solvedInc
                )
            }
        }

        // Evaluate badge unlocks based on new accomplishments
        val badges = profile.unlockedBadges.split(",").map { it.trim() }.toMutableSet()
        
        if (updatedProfile.totalCubesSolved >= 10 && !badges.contains("📦 Cube Architect")) {
            badges.add("📦 Cube Architect")
        }
        if (updatedProfile.totalCubesSolved >= 30 && !badges.contains("🏰 Master Builder")) {
            badges.add("🏰 Master Builder")
        }
        if (updatedProfile.totalMathSolved >= 10 && !badges.contains("📐 Math Squire")) {
            badges.add("📐 Math Squire")
        }
        if (updatedProfile.totalMathSolved >= 40 && !badges.contains("👑 Math Royalty")) {
            badges.add("👑 Math Royalty")
        }
        if (updatedProfile.totalWordsSolved >= 10 && !badges.contains("📚 Speller Apprentice")) {
            badges.add("📚 Speller Apprentice")
        }
        if (updatedProfile.totalWordsSolved >= 30 && !badges.contains("🧙 Word Wizard")) {
            badges.add("🧙 Word Wizard")
        }
        if (updatedProfile.totalSudokuSolved >= 1 && !badges.contains("🧩 Puzzle Novice")) {
            badges.add("🧩 Puzzle Novice")
        }
        if (updatedProfile.totalSudokuSolved >= 5 && !badges.contains("🧠 Sudoku Ninja")) {
            badges.add("🧠 Sudoku Ninja")
        }
        if (updatedProfile.streak >= 3 && !badges.contains("🔥 Streak Rookie")) {
            badges.add("🔥 Streak Rookie")
        }
        if (updatedProfile.streak >= 7 && !badges.contains("💫 Streak Champion")) {
            badges.add("💫 Streak Champion")
        }

        updatedProfile = updatedProfile.copy(
            unlockedBadges = badges.joinToString(", ")
        )

        dao.updateProfile(updatedProfile)
    }
}

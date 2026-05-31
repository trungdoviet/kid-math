package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "student_profiles")
data class StudentProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "Math Hero",
    val avatar: String = "🦁", // default avatar
    val streak: Int = 1,
    val lastActiveTime: Long = System.currentTimeMillis(),
    val coins: Int = 10, // starts with some pocket coins
    
    // Level progress status (0 = Not started, 10 = Max Level)
    val additionLevel: Int = 1,
    val subtractionLevel: Int = 1,
    val missingNumberLevel: Int = 1,
    val sudokuLevel: Int = 1,
    val wordMatchingLevel: Int = 1,
    val spellingLevel: Int = 1,
    val cubeCountingLevel: Int = 1,
    
    // Total questions solved statistics
    val totalMathSolved: Int = 0,
    val totalWordsSolved: Int = 0,
    val totalSudokuSolved: Int = 0,
    val totalCubesSolved: Int = 0,
    
    // Unlocked collections storing emoji characters separated by commas
    val unlockedAvatars: String = "🦁,🐨,🦊",
    val unlockedToys: String = "",
    val unlockedBadges: String = "⭐ Novice Apprenty"
)

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profiles LIMIT 1")
    fun getProfile(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profiles LIMIT 1")
    suspend fun getProfileSync(): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfile)

    @Update
    suspend fun updateProfile(profile: StudentProfile)
}

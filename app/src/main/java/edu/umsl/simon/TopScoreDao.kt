package edu.umsl.simon

import androidx.room.*

@Dao

interface TopScoreDao {
    @Query("SELECT * FROM topScores ORDER BY score DESC")
    suspend fun getTopScores(): List<RoomTopScore>

    @Query("SELECT * FROM topScores ORDER BY score DESC LIMIT 1")
    suspend fun getHighScore(): RoomTopScore

    @Query("DELETE FROM topScores where score NOT IN (SELECT score FROM topScores ORDER BY score DESC LIMIT 10)")
    suspend fun deleteTopScore()

    @Insert
    suspend fun insert(topScore: RoomTopScore)
}
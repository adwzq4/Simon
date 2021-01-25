package edu.umsl.simon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topScores",
    indices = [Index("score")]
)

data class RoomTopScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "creation_date")
    val creationDate: String
)
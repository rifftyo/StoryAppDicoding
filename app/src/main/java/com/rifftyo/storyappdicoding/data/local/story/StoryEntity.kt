package com.rifftyo.storyappdicoding.data.local.story

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
)
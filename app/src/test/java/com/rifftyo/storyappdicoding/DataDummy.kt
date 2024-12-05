package com.rifftyo.storyappdicoding

import com.rifftyo.storyappdicoding.data.local.story.StoryEntity

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()

        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "photoUrl $i",
                "author + $i",
                "description $i",
            )
            items.add(story)
        }
        return items
    }
}
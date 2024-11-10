package com.rifftyo.storyappdicoding.data.remote.response

data class DetailResponse(
	val error: Boolean? = null,
	val message: String? = null,
	val story: Story? = null
)

data class Story(
	val photoUrl: String? = null,
	val createdAt: String? = null,
	val name: String? = null,
	val description: String? = null,
	val lon: Any? = null,
	val id: String? = null,
	val lat: Any? = null
)


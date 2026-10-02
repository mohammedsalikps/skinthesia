package com.skinthesia.feature.community

import com.skinthesia.R

/** A fictional, presentation-only discussion card — never a real post, poster, or comment thread. */
data class CommunityDiscussion(
    val id: String,
    val titleRes: Int,
    val excerptRes: Int,
    val commentCount: Int,
)

val CommunityDiscussions: List<CommunityDiscussion> = listOf(
    CommunityDiscussion(id = "discussion-1", titleRes = R.string.community_discussion_1_title, excerptRes = R.string.community_discussion_1_excerpt, commentCount = 12),
    CommunityDiscussion(id = "discussion-2", titleRes = R.string.community_discussion_2_title, excerptRes = R.string.community_discussion_2_excerpt, commentCount = 8),
    CommunityDiscussion(id = "discussion-3", titleRes = R.string.community_discussion_3_title, excerptRes = R.string.community_discussion_3_excerpt, commentCount = 5),
)

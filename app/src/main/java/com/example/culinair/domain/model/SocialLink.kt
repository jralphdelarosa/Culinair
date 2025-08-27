package com.example.culinair.domain.model

import androidx.compose.ui.graphics.Color


/**
 * Created by John Ralph Dela Rosa on 8/28/2025.
 */
// Data classes for social links
data class SocialLink(
    val name: String,
    val url: String,
    val platform: SocialPlatform
) {
    val displayUrl: String get() = when (platform) {
        SocialPlatform.INSTAGRAM -> url.removePrefix("https://instagram.com/").removePrefix("https://www.instagram.com/")
        SocialPlatform.TWITTER -> url.removePrefix("https://twitter.com/").removePrefix("https://x.com/")
        SocialPlatform.WEBSITE -> url.removePrefix("https://").removePrefix("http://")
    }
}

enum class SocialPlatform(
    val displayName: String,
    val backgroundColor: Color
) {
    INSTAGRAM("Instagram", Color(0xFFE4405F)),
    TWITTER("Twitter", Color(0xFF1DA1F2)),
    WEBSITE("Website", Color(0xFF6366F1))
}
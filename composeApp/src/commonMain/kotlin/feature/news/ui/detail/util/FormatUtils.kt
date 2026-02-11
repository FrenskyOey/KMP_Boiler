package feature.news.ui.detail.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

object FormatUtils {
    
    fun formatRelativeTime(isoString: String): String {
        try {
            val instant = Instant.parse(isoString)
            val now = Clock.System.now()
            val duration = now - instant
            val seconds = duration.inWholeSeconds
            
            return when {
                seconds < 60 -> "just now"
                seconds < 3600 -> "${seconds / 60}m ago"
                seconds < 86400 -> "${seconds / 3600}h ago"
                seconds < 2592000 -> "${seconds / 86400}d ago" // < 30 days
                else -> {
                    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                    // Simple manual formatting to avoid platform specific DateTimeFormatter for now in KMP common
                    // Format: 02 Feb 2024
                    val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                    "${date.dayOfMonth.toString().padStart(2, '0')} $month ${date.year}"
                }
            }
        } catch (e: Exception) {
            return isoString // Fallback
        }
    }

    fun formatReadTime(minutes: Int): String {
        return "$minutes min read"
    }

    fun formatAuthorNameForAvatar(name: String): String {
        // Remove titles like Dr., Prof., Mr., Mrs.
        val titles = listOf("Dr.", "Prof.", "Mr.", "Mrs.", "Ms.")
        val parts = name.split(" ").filter { part -> 
            !titles.any { title -> part.equals(title, ignoreCase = true) }
        }
        return parts.joinToString(" ")
    }
}

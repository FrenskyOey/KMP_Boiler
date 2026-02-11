package feature.news.ui.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import core.components.CoreAvatar
import core.theme.*
import feature.news.domain.model.NewsDetail
import feature.news.ui.detail.util.FormatUtils

@Composable
fun NewsAuthor(
    author: NewsDetail.Author,
    publishedAt: String,
    readTime: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val cleanName = FormatUtils.formatAuthorNameForAvatar(author.name)
        CoreAvatar(
            imageUrl = author.avatar,
            name = cleanName, // Use cleaned name for initials
            size = Dimens.XXXL
        )
        
        Spacer(modifier = Modifier.width(Spacing.Small))
        
        Column {
            Text(
                text = author.name,
                style = getTextTitleMedium(),
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = author.publication,
                    style = getTextLabelMedium(),
                    color = getPrimaryColor(),
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = " • ${FormatUtils.formatRelativeTime(publishedAt)} • ",
                    style = getTextLabelMedium(),
                    color = getOnSurfaceVariantColor()
                )
                
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp), 
                    tint = getOnSurfaceVariantColor()
                )
                
                Spacer(modifier = Modifier.width(Spacing.Tiny))
                
                Text(
                    text = FormatUtils.formatReadTime(readTime),
                    style = getTextLabelMedium(),
                    color = getOnSurfaceVariantColor()
                )
            }
        }
    }
}

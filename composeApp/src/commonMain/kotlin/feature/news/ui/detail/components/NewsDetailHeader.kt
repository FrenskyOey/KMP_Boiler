package feature.news.ui.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import core.theme.*

@Composable
fun NewsDetailHeader(
    imageUrl: String?,
    category: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header Image
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(BorderRadius.Large)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // Topic/Category
        androidx.compose.material3.Surface(
            color = getPrimaryContainerColor(),
            shape = RoundedCornerShape(BorderRadius.Circle),
            modifier = Modifier.padding(bottom = Spacing.ExtraSmall)
        ) {
            Text(
                text = category.uppercase(),
                style = getTextLabelMedium(),
                color = getOnPrimaryContainerColor(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = Spacing.Small, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

        // Title
        Text(
            text = title,
            style = getTextHeadlineSmall(),
            fontWeight = FontWeight.Bold
        )
    }
}

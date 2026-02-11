package feature.news.ui.detail.components

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import core.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsDetailTags(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = spacedBy(Spacing.ExtraSmall),
        verticalArrangement = spacedBy(Spacing.ExtraSmall),
        modifier = modifier.fillMaxWidth()
    ) {
        tags.forEach { tag ->
            SuggestionChip(
                onClick = { /* No op */ },
                label = { Text(text = "#$tag") },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = getSurfaceVariantColor(),
                    labelColor = getOnSurfaceVariantColor()
                ),
                border = null 
            )
        }
    }
}

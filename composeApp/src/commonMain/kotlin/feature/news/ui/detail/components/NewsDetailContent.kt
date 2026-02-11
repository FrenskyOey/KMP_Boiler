package feature.news.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import core.theme.*
import feature.news.domain.model.NewsContent

@Composable
fun NewsDetailContent(
    content: List<NewsContent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        content.forEach { item ->
            when (item) {
                is NewsContent.Paragraph -> {
                    ParagraphView(item.text)
                    Spacer(modifier = Modifier.height(Spacing.Medium))
                }
                is NewsContent.Quote -> {
                    QuoteView(item)
                    Spacer(modifier = Modifier.height(Spacing.Large))
                }
            }
        }
    }
}

@Composable
private fun ParagraphView(text: String) {
    Text(
        text = text,
        style = getTextBodyLarge(),
        color = getOnSurfaceColor(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun QuoteView(quote: NewsContent.Quote) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.ExtraSmall)
            .height(IntrinsicSize.Min) // Match height of text
    ) {
        // Vertical Bar
        Box(
            modifier = Modifier
                .width(Spacing.Tiny)
                .fillMaxHeight()
                .background(getPrimaryColor(), RoundedCornerShape(Dimens.XXS)) // 2dp
        )
        
        Spacer(modifier = Modifier.width(Spacing.Small))
        
        Text(
            text = quote.text,
            style = getTextBodyLarge().copy(fontStyle = FontStyle.Italic),
            color = getOnSurfaceVariantColor(),
            modifier = Modifier.weight(1f)
        )
    }
}

package feature.onboarding.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.theme.*

/**
 * Login Header - App icon and welcome text
 */
@Composable
fun LoginHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Icon(
            imageVector = Icons.Default.Receipt, // Using Receipt icon as placeholder for News app
            contentDescription = null,
            modifier = Modifier.size(ComponentDimens.IconSizeLarge),
            tint = getPrimaryColor()
        )
        
        Spacer(modifier = Modifier.height(Spacing.Medium))
        
        // Title
        Text(
            text = "Welcome Back",
            style = getTextHeadlineLarge().copy(fontWeight = FontWeight.Bold),
            color = getOnBackgroundColor(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Spacing.Small))
        
        // Subtitle
        Text(
            text = "Enter your credentials to access your personalized news feed.",
            style = getTextBodyMedium(),
            color = getOnSurfaceVariantColor(),
            textAlign = TextAlign.Center
        )
    }
}

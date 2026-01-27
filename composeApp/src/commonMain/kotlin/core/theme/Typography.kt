package core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

// Material 3 Typography helpers with extension functions
@Composable fun getTextDisplayLarge() = MaterialTheme.typography.displayLarge
@Composable fun getTextDisplayMedium() = MaterialTheme.typography.displayMedium
@Composable fun getTextDisplaySmall() = MaterialTheme.typography.displaySmall
@Composable fun getTextHeadlineLarge() = MaterialTheme.typography.headlineLarge
@Composable fun getTextHeadlineMedium() = MaterialTheme.typography.headlineMedium
@Composable fun getTextHeadlineSmall() = MaterialTheme.typography.headlineSmall
@Composable fun getTextTitleLarge() = MaterialTheme.typography.titleLarge
@Composable fun getTextTitleMedium() = MaterialTheme.typography.titleMedium
@Composable fun getTextTitleSmall() = MaterialTheme.typography.titleSmall
@Composable fun getTextBodyLarge() = MaterialTheme.typography.bodyLarge
@Composable fun getTextBodyMedium() = MaterialTheme.typography.bodyMedium
@Composable fun getTextBodySmall() = MaterialTheme.typography.bodySmall
@Composable fun getTextLabelLarge() = MaterialTheme.typography.labelLarge
@Composable fun getTextLabelMedium() = MaterialTheme.typography.labelMedium
@Composable fun getTextLabelSmall() = MaterialTheme.typography.labelSmall

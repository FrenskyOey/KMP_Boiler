package core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// Material 3 Color scheme with extension functions
@Composable fun getPrimaryColor() = MaterialTheme.colorScheme.primary
@Composable fun getOnPrimaryColor() = MaterialTheme.colorScheme.onPrimary
@Composable fun getPrimaryContainerColor() = MaterialTheme.colorScheme.primaryContainer
@Composable fun getOnPrimaryContainerColor() = MaterialTheme.colorScheme.onPrimaryContainer
@Composable fun getSecondaryColor() = MaterialTheme.colorScheme.secondary
@Composable fun getOnSecondaryColor() = MaterialTheme.colorScheme.onSecondary
@Composable fun getSecondaryContainerColor() = MaterialTheme.colorScheme.secondaryContainer
@Composable fun getOnSecondaryContainerColor() = MaterialTheme.colorScheme.onSecondaryContainer
@Composable fun getTertiaryColor() = MaterialTheme.colorScheme.tertiary
@Composable fun getBackgroundColor() = MaterialTheme.colorScheme.background
@Composable fun getOnBackgroundColor() = MaterialTheme.colorScheme.onBackground
@Composable fun getSurfaceColor() = MaterialTheme.colorScheme.surface
@Composable fun getOnSurfaceColor() = MaterialTheme.colorScheme.onSurface
@Composable fun getSurfaceVariantColor() = MaterialTheme.colorScheme.surfaceVariant
@Composable fun getErrorColor() = MaterialTheme.colorScheme.error
@Composable fun getOnErrorColor() = MaterialTheme.colorScheme.onError

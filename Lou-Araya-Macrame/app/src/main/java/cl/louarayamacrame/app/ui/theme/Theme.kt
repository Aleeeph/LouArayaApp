package cl.louarayamacrame.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Paleta generada a partir del color semilla verde azulado (#2E7D6B) con acento naranja.
private val LightColors = lightColorScheme(
    primary = Color(0xFF1F6B5B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA8F1DC),
    onPrimaryContainer = Color(0xFF002019),
    secondary = Color(0xFF4B635B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCDE8DE),
    onSecondaryContainer = Color(0xFF072019),
    tertiary = Color(0xFFB35A1F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBC6),
    onTertiaryContainer = Color(0xFF381300),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFBF7F0),
    onBackground = Color(0xFF1B1C1A),
    surface = Color(0xFFFBF7F0),
    onSurface = Color(0xFF1B1C1A),
    surfaceVariant = Color(0xFFDBE5DF),
    onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975),
    outlineVariant = Color(0xFFBFC9C4),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF6F2EA),
    surfaceContainer = Color(0xFFF0EDE5),
    surfaceContainerHigh = Color(0xFFEAE7DF),
    surfaceContainerHighest = Color(0xFFE4E2DA)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DD8C3),
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005143),
    onPrimaryContainer = Color(0xFFA8F1DC),
    secondary = Color(0xFFB1CCC2),
    onSecondary = Color(0xFF1D352E),
    secondaryContainer = Color(0xFF334B44),
    onSecondaryContainer = Color(0xFFCDE8DE),
    tertiary = Color(0xFFFFB68A),
    onTertiary = Color(0xFF552200),
    tertiaryContainer = Color(0xFF7B3A0A),
    onTertiaryContainer = Color(0xFFFFDBC6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121412),
    onBackground = Color(0xFFE3E3DF),
    surface = Color(0xFF121412),
    onSurface = Color(0xFFE3E3DF),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF89938F),
    outlineVariant = Color(0xFF3F4945),
    surfaceContainerLowest = Color(0xFF0D0F0D),
    surfaceContainerLow = Color(0xFF1A1C1A),
    surfaceContainer = Color(0xFF1E201E),
    surfaceContainerHigh = Color(0xFF282B28),
    surfaceContainerHighest = Color(0xFF333533)
)

private val AppTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun LouArayaMacrameTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

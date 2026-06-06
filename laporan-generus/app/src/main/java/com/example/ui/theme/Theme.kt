package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Simple high contrast white scheme optimized for elderly readability
private val LightColorScheme = lightColorScheme(
    primary = BoldPrimaryColor,                 // High contrast deep slate
    onPrimary = WhitePure,                      // White text on dark buttons
    primaryContainer = Color(0xFFE2E8F0),       // Very clear distinct grey box
    onPrimaryContainer = DarkTextBlack,         // Extremely clear dark text on containers
    
    secondary = HighlightSecondary,             // High contrast deep teal for action buttons/states
    onSecondary = WhitePure,
    secondaryContainer = Color(0xFFF1F5F9),     // Clean neutral container
    onSecondaryContainer = DarkTextBlack,
    
    tertiary = HighlightTertiary,               // Warm high contrast accent/status indicator
    onTertiary = WhitePure,
    tertiaryContainer = Color(0xFFFEF3C7),      // Warm box for reminders
    onTertiaryContainer = SecondaryTextDark,
    
    background = WhiteSoftBackground,           // Super clean neutral background
    surface = WhitePure,                        // Clean crisp white cards
    onBackground = DarkTextBlack,               // High contrast text black
    onSurface = DarkTextBlack,                  // High contrast text black
    
    outline = Color(0xFF64748B),                // Distinct, clear outlines
    outlineVariant = SlateBorder                // Soft distinct divider lines
)

// Minimal dark mode as robust fallback
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF8FAFC),                // Bright high-contrast white text/elements
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF334155),
    onPrimaryContainer = Color(0xFFF1F5F9),
    
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFF1F5F9),
    
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    
    background = Color(0xFF0F172A),             // Charcoal/Slate background
    surface = Color(0xFF1E293B),                // Lighter slate cards
    onBackground = Color(0xFFF8FAFC),           // Clear white text
    onSurface = Color(0xFFF8FAFC),
    
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

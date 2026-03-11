package com.example.eventcoord.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = DoradoPrincipal, // Tus botones principales brillarán en dorado
    onPrimary = Color.White, // El texto dentro de los botones dorados
    secondary = DoradoClaroBrillante,
    background = AzulOscuroIntenso, // El fondo principal de la app
    surface = AzulMarinoProfundo, // El color de las tarjetas (Cards)
    onBackground = BlancoCalido, // El texto general sobre el fondo azul
    onSurface = BlancoCalido, // El texto sobre las tarjetas
    surfaceVariant = AzulDegradadoMedio, // Para la barra de navegación inferior
    onSurfaceVariant = DoradoClaroBrillante // Íconos activos en la barra
)

private val LightColorScheme = lightColorScheme(
    primary = AzulMarinoProfundo, // En modo claro, los botones pueden ser azules
    onPrimary = Color.White,
    secondary = DoradoPrincipal,
    background = BlancoCalido, // Fondo general súper limpio
    surface = Color.White, // Tarjetas blancas para que resalten
    onBackground = AzulOscuroIntenso, // Texto principal oscuro
    onSurface = AzulOscuroIntenso,
    surfaceVariant = GrisMuyClaro, // La barra inferior
    onSurfaceVariant = AzulMarinoProfundo
)

@Composable
fun EventCoordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
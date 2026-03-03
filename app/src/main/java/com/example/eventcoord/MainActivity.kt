package com.example.eventcoord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.eventcoord.ui.AppNavigation
import com.example.eventcoord.ui.theme.EventCoordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val sharedPrefs = remember { context.getSharedPreferences("ThemePrefs", MODE_PRIVATE) }
            var actualTheme by remember { mutableStateOf(sharedPrefs.getString("tema_app", "Igual que el sistema")) } // Leemos el tema actual
            // Listener que avisa cuando cambia el tema de la app
            DisposableEffect(context) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                    if (key == "tema_app") {
                        actualTheme = prefs.getString("tema_app", "Igual que el sistema")
                    }
                }
                sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)}
            }
            // Traducimos textos a booleanos para los temas
            val useDarkTheme = when (actualTheme){
                "Modo claro" -> false
                "Modo oscuro" -> true
                else -> isSystemInDarkTheme() // Igual que el sistema
            }
            EventCoordTheme(darkTheme = useDarkTheme) {
                // Llamamos al gestor de navegacion
                AppNavigation()
            }
        }
    }
}
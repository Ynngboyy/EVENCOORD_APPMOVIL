package com.example.eventcoord.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcoord.ui.screens.home.EventScreen
import com.example.eventcoord.ui.screens.home.HomeScreen
import com.example.eventcoord.ui.screens.home.NewEventScreen
import com.example.eventcoord.ui.screens.home.ProfileScreen
import com.example.eventcoord.ui.screens.login.LoginScreen
import com.example.eventcoord.ui.screens.login.PasswordScreen
import com.example.eventcoord.ui.screens.login.RegistrationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Definimos las rutas
    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { // Animacion cuando entras a una pantalla
            slideIntoContainer( // Deslizamiento de la pantalla
                towards = AnimatedContentTransitionScope.SlideDirection.Left, // Direccion de deslizamiento
                animationSpec = tween(750) // Duracion de deslizamiento
            )
        },
        exitTransition = { // Animacion cuando sales de una pantalla
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(750)
            )
        },
        popEnterTransition = { // Animacion cuando regresas la pantalla anterior entra desde la izquierda
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(750)
            )
        },
        popExitTransition = { // Animacion cuando regresas la pantalla actual sale hacia la izquierda
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(750)
            )
        }
    ) {
        // Ruta 1: Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onForgotPassword = { navController.navigate("password") },
                onRegister = { navController.navigate("register") }
            )
        }
        // Ruta 2: Home
        composable("home") {
            HomeScreen(
                onEvent = { navController.navigate("event") },
                onNewevent = {navController.navigate("newevent")},
                onProfile = {navController.navigate("profile")}
            )
        }
        // Ruta 3: Password
        composable("password") {
            PasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        // Ruta 4: Registration
        composable("register"){
            RegistrationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        // Ruta 5: Event
        composable  ("event"){
            EventScreen(
                onBackClick = {navController.popBackStack()}
            )
        }
        // Ruta 6: NewEvent
        composable ("newevent"){
            NewEventScreen(
                onBackClick = {navController.popBackStack()}
            )
        }
        // Ruta 7: Profile
        composable ("profile"){
            ProfileScreen(
                onLogOut = {navController.navigate("login"){popUpTo(0){inclusive = true} } },
                onBackClick = {navController.popBackStack()}
            )
        }
    }
}
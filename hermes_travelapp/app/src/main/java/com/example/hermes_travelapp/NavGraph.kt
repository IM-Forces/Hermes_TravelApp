package com.example.hermes_travelapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hermes_travelapp.ui.screens.HomeScreen
import com.example.hermes_travelapp.ui.screens.LoginScreen
import com.example.hermes_travelapp.ui.screens.ProfileScreen
import com.example.hermes_travelapp.ui.screens.RegisterScreen
import com.example.hermes_travelapp.ui.screens.SplashScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash", modifier = modifier) {
        composable("splash") { SplashScreen(navController = navController) }
        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterClick = { navController.navigate("home") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("home") { HomeScreen(navController = navController) }
        composable("profile") { ProfileScreen() }
    }
}
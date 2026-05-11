package com.example.parkingandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parkingandroid.ui.screens.*
import com.example.parkingandroid.ui.theme.ParkingAndroidTheme
import com.example.parkingandroid.ui.viewmodel.AuthViewModel
import com.example.parkingandroid.ui.viewmodel.ParkingViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Anasayfa", Icons.Default.Home)
    object Map : Screen("map", "Otopark", Icons.Default.Place)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)
    object Login : Screen("login", "Giriş", Icons.Default.Person)
    object Reservation : Screen("reservation/{spotId}", "Rezervasyon", Icons.Default.Place)
}

class MainActivity : ComponentActivity() {
    private val parkingViewModel: ParkingViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkingAndroidTheme {
                val navController = rememberNavController()
                val spots by parkingViewModel.spots.collectAsState()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = currentDestination?.route in listOf(Screen.Home.route, Screen.Map.route, Screen.Profile.route)

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                val items = listOf(Screen.Home, Screen.Map, Screen.Profile)
                                items.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = null) },
                                        label = { Text(screen.label) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController, 
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(onTimeout = {
                                if (isLoggedIn) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            })
                        }
                        composable(Screen.Login.route) {
                            LoginScreen(onLoginSuccess = { email, pass ->
                                val success = authViewModel.login(email, pass)
                                if (success) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                                success
                            })
                        }
                        composable(Screen.Home.route) { HomeScreen() }
                        composable(Screen.Map.route) { 
                            MainScreen(
                                spots = spots,
                                onSpotClick = { spotId -> 
                                    parkingViewModel.reserveSpot(spotId)
                                    navController.navigate("reservation/$spotId")
                                }
                            ) 
                        }
                        composable(
                            route = Screen.Reservation.route,
                            arguments = listOf(navArgument("spotId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val spotId = backStackEntry.arguments?.getInt("spotId") ?: 0
                            ReservationStatusScreen(
                                spotId = spotId,
                                onBackToMap = {
                                    navController.navigate(Screen.Map.route) {
                                        popUpTo(Screen.Map.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Profile.route) { 
                            ProfileScreen(onLogout = {
                                authViewModel.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }) 
                        }
                    }
                }
            }
        }
    }
}

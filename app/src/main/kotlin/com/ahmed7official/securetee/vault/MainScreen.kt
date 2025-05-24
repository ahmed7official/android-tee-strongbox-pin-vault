package com.ahmed7official.securetee.vault

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ahmed7official.securetee.vault.feature.add.AddPinScreen
import com.ahmed7official.securetee.vault.feature.home.HomeScreen
import com.ahmed7official.securetee.vault.ui.dialog.PinDetailsScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToAddPin = { navController.navigate("add_pin") },
                onPinSelected = { pinId -> 
                    navController.navigate("pin_details/$pinId")
                }
            )
        }
        
        composable("add_pin") {
            AddPinScreen(
                onPinAdded = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "pin_details/{pinId}",
            arguments = listOf(
                navArgument("pinId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pinId = backStackEntry.arguments?.getString("pinId") ?: ""
            PinDetailsScreen(
                pinId = pinId,
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
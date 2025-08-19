package com.smartgrocery.pantry.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val appState = rememberAppState()
    appState.refresh()

    val destinations = listOf(
        BottomDestination.Inventory,
        BottomDestination.Expiring,
        BottomDestination.MealPlan,
        BottomDestination.ShoppingList,
        BottomDestination.Scan,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomDestination.Inventory.route,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(BottomDestination.Inventory.route) { InventoryScreen(appState) }
            composable(BottomDestination.Expiring.route) { ExpiringScreen(appState) }
            composable(BottomDestination.MealPlan.route) { MealPlanScreen(appState) }
            composable(BottomDestination.ShoppingList.route) { ShoppingListScreen(appState) }
            composable(BottomDestination.Scan.route) { ReceiptScanScreen(appState) }
        }
    }
}

sealed class BottomDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Inventory : BottomDestination("inventory", "Pantry", androidx.compose.material.icons.Icons.Outlined.Kitchen)
    data object Expiring : BottomDestination("expiring", "Expiring", androidx.compose.material.icons.Icons.Outlined.Schedule)
    data object MealPlan : BottomDestination("mealplan", "Meal Plan", androidx.compose.material.icons.Icons.Outlined.CalendarMonth)
    data object ShoppingList : BottomDestination("shopping", "Shop", androidx.compose.material.icons.Icons.Outlined.ShoppingCart)
    data object Scan : BottomDestination("scan", "Scan", androidx.compose.material.icons.Icons.Outlined.DocumentScanner)
}

@Composable fun InventoryScreen(app: AppState) { InventoryList(app) }
@Composable fun ExpiringScreen(app: AppState) { ExpiringList(app) }
@Composable fun MealPlanScreen(app: AppState) { MealPlanList(app) }
@Composable fun ShoppingListScreen(app: AppState) { ShoppingList(app) }
@Composable fun ReceiptScanScreen(app: AppState) { ReceiptScanStub(app) }


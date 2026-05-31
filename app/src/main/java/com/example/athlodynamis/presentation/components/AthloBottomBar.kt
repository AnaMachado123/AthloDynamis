package com.example.athlodynamis.presentation.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.navigation.Screen

@Composable
fun AthloBottomBar(
    navController: NavController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(28.dp))
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Events.route,
            onClick = { navController.navigate(Screen.Events.route) },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Eventos") },
            label = { Text("Eventos") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Teams.route,
            onClick = { navController.navigate(Screen.Teams.route) },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Equipas") },
            label = { Text("Equipas") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Stats.route,
            onClick = { navController.navigate(Screen.Stats.route) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
            label = { Text("Stats") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Notifications.route,
            onClick = { navController.navigate(Screen.Notifications.route) },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notif.") },
            label = { Text("Notif.") },
            colors = athloBottomBarItemColors()
        )
    }
}

@Composable
private fun athloBottomBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AthloColors.Blue,
    selectedTextColor = AthloColors.Blue,
    indicatorColor = AthloColors.SoftBlue,
    unselectedIconColor = AthloColors.TextMuted,
    unselectedTextColor = AthloColors.TextMuted
)
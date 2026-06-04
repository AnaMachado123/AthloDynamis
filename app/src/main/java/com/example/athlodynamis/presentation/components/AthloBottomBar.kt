package com.example.athlodynamis.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.NotificationsViewModel

enum class AthloUserRole {
    PLAYER,
    ORGANIZER,
    ADMIN
}

@Composable
fun AthloBottomBar(
    navController: NavController,
    currentRoute: String,
    userRole: AthloUserRole = AthloUserRole.ADMIN
) {
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

    LaunchedEffect(currentRoute) {
        notificationsViewModel.loadNotifications()
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(28.dp))
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início") },
            colors = athloBottomBarItemColors()
        )

        if (userRole == AthloUserRole.ADMIN) {
            NavigationBarItem(
                selected = currentRoute == Screen.Management.route,
                onClick = {
                    navController.navigate(Screen.Management.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Gestão"
                    )
                },
                label = { Text("Gestão") },
                colors = athloBottomBarItemColors()
            )
        }

        NavigationBarItem(
            selected = currentRoute == Screen.Events.route,
            onClick = {
                navController.navigate(Screen.Events.route) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Eventos"
                )
            },
            label = { Text("Eventos") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Teams.route,
            onClick = {
                navController.navigate(Screen.Teams.route) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "Equipas"
                )
            },
            label = { Text("Equipas") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Stats.route,
            onClick = {
                navController.navigate(Screen.Stats.route) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Stats"
                )
            },
            label = { Text("Stats") },
            colors = athloBottomBarItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Notifications.route,
            onClick = {
                navController.navigate(Screen.Notifications.route) {
                    launchSingleTop = true
                }
            },
            icon = {
                NotificationIconWithBadge(
                    showBadge = unreadCount > 0 &&
                            currentRoute != Screen.Notifications.route
                )
            },
            label = { Text("Notif.") },
            colors = athloBottomBarItemColors()
        )
    }
}

@Composable
private fun NotificationIconWithBadge(
    showBadge: Boolean
) {
    Box {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notif."
        )

        if (showBadge) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .background(Color(0xFFE53935), CircleShape)
            )
        }
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
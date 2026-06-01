package com.example.athlodynamis.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.navigation.Screen

data class AthloNotification(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    val dayGroup: String,
    val type: NotificationType,
    val isUnread: Boolean
)

enum class NotificationType {
    GOAL,
    TEAM,
    GAME,
    RESULT,
    TOURNAMENT,
    TROPHY
}

@Composable
fun NotificationsScreen(navController: NavController) {
    val notifications = remember {
        listOf(
            AthloNotification(
                id = 1,
                title = "Golo da Equipa 1 vs Equipa 2",
                description = "Rui Moreira marcou aos 33'.",
                time = "Agora mesmo",
                dayGroup = "HOJE",
                type = NotificationType.GOAL,
                isUnread = true
            ),
            AthloNotification(
                id = 2,
                title = "Foste adicionado a uma equipa",
                description = "SC Virius adicionou-te para o Torneio Regional Futsal.",
                time = "Há 25 min",
                dayGroup = "HOJE",
                type = NotificationType.TEAM,
                isUnread = true
            ),
            AthloNotification(
                id = 3,
                title = "Jogo daqui a 2 horas",
                description = "Equipa 1 vs Equipa 2 · Pavilhão Municipal.",
                time = "10:15",
                dayGroup = "HOJE",
                type = NotificationType.GAME,
                isUnread = true
            ),
            AthloNotification(
                id = 4,
                title = "Resultado registado",
                description = "SL Benficas 3 - 1 CD Lanheses.",
                time = "Ontem · 19:20",
                dayGroup = "ONTEM",
                type = NotificationType.RESULT,
                isUnread = false
            ),
            AthloNotification(
                id = 5,
                title = "Novo torneio disponível",
                description = "Torneio de Voleibol Urbano · inscrições abertas até 20 Junho.",
                time = "Ontem · 17:45",
                dayGroup = "ONTEM",
                type = NotificationType.TOURNAMENT,
                isUnread = false
            ),
            AthloNotification(
                id = 6,
                title = "Conquistaste um troféu!",
                description = "1.º Lugar · Liga Regional 2026.",
                time = "Ontem · 09:02",
                dayGroup = "ONTEM",
                type = NotificationType.TROPHY,
                isUnread = false
            )
        )
    }

    val unreadCount = notifications.count { it.isUnread }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Notifications.route
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                NotificationsHeader(
                    unreadCount = unreadCount
                )
            }

            val groupedNotifications = notifications.groupBy { it.dayGroup }

            groupedNotifications.forEach { (day, items) ->
                item {
                    SectionTitle(title = day)
                }

                items(items.size) { index ->
                    NotificationCard(
                        notification = items[index]
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsHeader(
    unreadCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthloColors.Navy)
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notificações",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (unreadCount == 1) {
                            "1 notificação por ler"
                        } else {
                            "$unreadCount notificações por ler"
                        },
                        color = Color(0xFF8EC5F4),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(AthloColors.Blue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GM",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextMuted,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun NotificationCard(
    notification: AthloNotification
) {
    val colors = notificationColors(notification.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(
            if (notification.isUnread) 26.dp else AthloRadius.Large
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isUnread) {
                Color(0xFFFFFFFF)
            } else {
                AthloColors.CardWhite
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isUnread) 8.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (notification.isUnread) Color(0xFFFDFEFF) else Color.White
                )
                .padding(
                    horizontal = if (notification.isUnread) 20.dp else 18.dp,
                    vertical = if (notification.isUnread) 20.dp else 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (notification.isUnread) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(54.dp)
                        .background(AthloColors.Blue, RoundedCornerShape(999.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))
            }

            Box(
                modifier = Modifier
                    .size(if (notification.isUnread) 46.dp else 42.dp)
                    .background(colors.background, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificationIcon(notification.type),
                    contentDescription = notification.title,
                    tint = colors.icon,
                    modifier = Modifier.size(if (notification.isUnread) 24.dp else 21.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )

                    if (notification.isUnread) {
                        Spacer(modifier = Modifier.width(8.dp))

                        NewBadge()
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = notification.description,
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.time,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun NewBadge() {
    Box(
        modifier = Modifier
            .background(AthloColors.Blue, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nova",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class NotificationVisualColors(
    val background: Color,
    val icon: Color
)

private fun notificationColors(type: NotificationType): NotificationVisualColors {
    return when (type) {
        NotificationType.GOAL -> NotificationVisualColors(
            background = AthloColors.DangerBg,
            icon = Color(0xFFC83755)
        )

        NotificationType.TEAM -> NotificationVisualColors(
            background = AthloColors.SoftBlue,
            icon = AthloColors.Blue
        )

        NotificationType.GAME -> NotificationVisualColors(
            background = Color(0xFFEAF4FF),
            icon = AthloColors.Blue
        )

        NotificationType.RESULT -> NotificationVisualColors(
            background = AthloColors.SuccessBg,
            icon = Color(0xFF3F7A28)
        )

        NotificationType.TOURNAMENT -> NotificationVisualColors(
            background = AthloColors.InfoBg,
            icon = AthloColors.Blue
        )

        NotificationType.TROPHY -> NotificationVisualColors(
            background = AthloColors.WarningBg,
            icon = Color(0xFF9A6B22)
        )
    }
}

private fun notificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.GOAL -> Icons.Default.SportsSoccer
        NotificationType.TEAM -> Icons.Default.GroupAdd
        NotificationType.GAME -> Icons.Default.Timer
        NotificationType.RESULT -> Icons.Default.CheckCircle
        NotificationType.TOURNAMENT -> Icons.Default.CalendarMonth
        NotificationType.TROPHY -> Icons.Default.EmojiEvents
    }
}
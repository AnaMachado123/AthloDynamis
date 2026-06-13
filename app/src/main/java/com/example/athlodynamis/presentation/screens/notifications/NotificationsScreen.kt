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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.domain.model.Notification
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.NotificationsViewModel

enum class NotificationType {
    GOAL,
    TEAM,
    GAME,
    RESULT,
    TOURNAMENT,
    TROPHY,
    INFO
}

@Composable
fun NotificationsScreen(
    navController: NavController,
    userRole: AthloUserRole,
    currentUserId: String
) {
    val viewModel: NotificationsViewModel = viewModel()

    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.loadNotifications(
            currentUserId = currentUserId
        )
    }


    val unreadCount = notifications.count { !it.isRead }
    val loadingText = stringResource(R.string.notifications_loading)
    val loadErrorText = stringResource(R.string.notifications_load_error)
    val recentText = stringResource(R.string.notifications_recent)

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Notifications.route,
                userRole = userRole
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

            when {
                isLoading -> {
                    item {
                        InfoCard(text = loadingText)
                    }
                }

                error != null -> {
                    item {
                        InfoCard(text = error ?: loadErrorText)
                    }
                }

                notifications.isEmpty() -> {
                    item {
                        EmptyNotificationsCard()
                    }
                }

                else -> {
                    val groupedNotifications = notifications.groupBy {
                        dayGroupFromCreatedAt(it.createdAt, recentText)
                    }

                    groupedNotifications.forEach { (day, items) ->
                        item {
                            SectionTitle(title = day)
                        }

                        items(items.size) { index ->
                            val notification = items[index]

                            NotificationCard(
                                notification = notification,
                                onClick = {
                                    if (!notification.isRead) {
                                        viewModel.markAsRead(notification.id)
                                    }
                                }
                            )
                        }
                    }
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
                        text = stringResource(R.string.notifications_title),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (unreadCount) {
                            0 -> stringResource(R.string.notifications_unread_zero)
                            1 -> stringResource(R.string.notifications_unread_one)
                            else -> stringResource(R.string.notifications_unread_many, unreadCount)
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
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notifications_title),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun EmptyNotificationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications_empty_cd),
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.notifications_empty_title),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.notifications_empty_desc),
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )
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
    notification: Notification,
    onClick: () -> Unit
) {
    val type = notificationTypeFromText(
        title = notification.title,
        message = notification.message
    )

    val colors = notificationColors(type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(
            if (!notification.isRead) 26.dp else AthloRadius.Large
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) {
                Color.White
            } else {
                AthloColors.CardWhite
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!notification.isRead) 8.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!notification.isRead) Color(0xFFFDFEFF) else Color.White
                )
                .padding(
                    horizontal = if (!notification.isRead) 20.dp else 18.dp,
                    vertical = if (!notification.isRead) 20.dp else 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!notification.isRead) {
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
                    .size(if (!notification.isRead) 46.dp else 42.dp)
                    .background(colors.background, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificationIcon(type),
                    contentDescription = notification.title,
                    tint = colors.icon,
                    modifier = Modifier.size(if (!notification.isRead) 24.dp else 21.dp)
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
                        text = localizedNotificationTitle(notification),
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))

                        NewBadge()
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = localizedNotificationTitle(notification),
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatNotificationTime(notification.createdAt, stringResource(R.string.notifications_no_date)),
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
            text = stringResource(R.string.notifications_new),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun localizedNotificationText(text: String): String {
    return text
        .replace("Novo evento criado", stringResource(R.string.notifications_text_new_event_created))
        .replace("Evento atualizado", stringResource(R.string.notifications_text_event_updated))
        .replace("Novo jogo agendado", stringResource(R.string.notifications_text_new_match_scheduled))
        .replace("Jogo atualizado", stringResource(R.string.notifications_text_match_updated))
        .replace("Pedido aprovado", stringResource(R.string.notifications_text_request_approved))
        .replace("Pedido rejeitado", stringResource(R.string.notifications_text_request_rejected))
        .replace("Golo", stringResource(R.string.live_event_goal))
        .replace("Assistência", stringResource(R.string.live_event_assist))
        .replace("Cartão amarelo", stringResource(R.string.live_event_yellow_card))
        .replace("Cartão vermelho", stringResource(R.string.live_event_red_card))
        .replace("Substituição", stringResource(R.string.live_event_substitution))
        .replace("foi atualizado", stringResource(R.string.notifications_text_was_updated))
        .replace("foi criado", stringResource(R.string.notifications_text_was_created))
        .replace("Foi criado", stringResource(R.string.notifications_text_was_created_capitalized))
        .replace("torneio", stringResource(R.string.notifications_text_tournament))
        .replace("modalidade", stringResource(R.string.notifications_text_sport_category))
        .replace("jogo", stringResource(R.string.notifications_text_match))
        .replace("aos", stringResource(R.string.notifications_text_at_minute))
}

private data class NotificationVisualColors(
    val background: Color,
    val icon: Color
)

private fun notificationTypeFromText(
    title: String,
    message: String
): NotificationType {
    val text = "$title $message".lowercase()

    return when {
        "golo" in text || "marcou" in text -> NotificationType.GOAL
        "assistência" in text || "assistencia" in text -> NotificationType.GAME
        "cartão" in text || "cartao" in text -> NotificationType.GAME
        "substituição" in text || "substituicao" in text -> NotificationType.GAME
        "equipa" in text || "adicionado" in text || "jogador" in text -> NotificationType.TEAM
        "jogo" in text || "partida" in text -> NotificationType.GAME
        "resultado" in text || "terminado" in text -> NotificationType.RESULT
        "vitória" in text || "vitoria" in text || "empate" in text -> NotificationType.RESULT
        "torneio" in text || "evento" in text -> NotificationType.TOURNAMENT
        "troféu" in text || "trofeu" in text || "trophy" in text || "1.º" in text -> NotificationType.TROPHY
        else -> NotificationType.INFO
    }
}

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

        NotificationType.INFO -> NotificationVisualColors(
            background = AthloColors.NeutralBg,
            icon = AthloColors.TextSecondary
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
        NotificationType.INFO -> Icons.Default.Notifications
    }
}

private fun dayGroupFromCreatedAt(createdAt: String?, recentText: String): String {
    return recentText
}

private fun formatNotificationTime(createdAt: String?, noDateText: String): String {
    if (createdAt.isNullOrBlank()) {
        return noDateText
    }

    val cleanDate = createdAt
        .replace("T", " ")
        .replace("Z", "")

    return cleanDate.take(16)
}

@Composable
private fun localizedNotificationTitle(
    notification: Notification
): String {
    val data = notification.data.orEmpty()

    return when (notification.notificationType) {
        "LIVE_GOAL" -> stringResource(
            R.string.live_notification_goal_title,
            data["teamName"].orEmpty()
        )

        "LIVE_ASSIST" -> stringResource(
            R.string.live_notification_assist_title
        )

        "LIVE_YELLOW_CARD" -> stringResource(
            R.string.live_notification_yellow_card_title
        )

        "LIVE_RED_CARD" -> stringResource(
            R.string.live_notification_red_card_title
        )

        "LIVE_SUBSTITUTION" -> stringResource(
            R.string.live_notification_substitution_title,
            data["teamName"].orEmpty()
        )

        "MATCH_DRAW" -> stringResource(
            R.string.offline_sync_draw_title
        )

        "MATCH_WIN" -> stringResource(
            R.string.offline_sync_win_title,
            data["winnerName"].orEmpty()
        )

        else -> localizedNotificationText(notification.title)
    }
}

@Composable
private fun localizedNotificationMessage(
    notification: Notification
): String {
    val data = notification.data.orEmpty()

    return when (notification.notificationType) {
        "LIVE_GOAL" -> stringResource(
            R.string.live_notification_goal_message,
            data["playerName"].orEmpty(),
            data["teamName"].orEmpty(),
            data["minute"]?.toIntOrNull() ?: 0,
            data["teamAName"].orEmpty(),
            data["scoreA"]?.toIntOrNull() ?: 0,
            data["scoreB"]?.toIntOrNull() ?: 0,
            data["teamBName"].orEmpty()
        )

        "LIVE_ASSIST" -> stringResource(
            R.string.live_notification_assist_message,
            data["playerName"].orEmpty(),
            data["teamName"].orEmpty(),
            data["minute"]?.toIntOrNull() ?: 0
        )

        "LIVE_YELLOW_CARD" -> stringResource(
            R.string.live_notification_yellow_card_message,
            data["playerName"].orEmpty(),
            data["teamName"].orEmpty(),
            data["minute"]?.toIntOrNull() ?: 0
        )

        "LIVE_RED_CARD" -> stringResource(
            R.string.live_notification_red_card_message,
            data["playerName"].orEmpty(),
            data["teamName"].orEmpty(),
            data["minute"]?.toIntOrNull() ?: 0
        )

        "LIVE_SUBSTITUTION" -> stringResource(
            R.string.live_notification_substitution_message,
            data["teamName"].orEmpty(),
            data["minute"]?.toIntOrNull() ?: 0,
            data["playerName"].orEmpty(),
            data["secondaryPlayerName"].orEmpty()
        )

        "MATCH_WIN" -> stringResource(
            R.string.offline_sync_win_message,
            data["winnerName"].orEmpty(),
            data["loserName"].orEmpty(),
            data["winnerScore"]?.toIntOrNull() ?: 0,
            data["loserScore"]?.toIntOrNull() ?: 0
        )

        "MATCH_DRAW" -> stringResource(
            R.string.offline_sync_draw_message,
            data["teamAName"].orEmpty(),
            data["teamBName"].orEmpty(),
            data["scoreA"]?.toIntOrNull() ?: 0,
            data["scoreB"]?.toIntOrNull() ?: 0
        )

        else -> localizedNotificationText(notification.message)
    }
}
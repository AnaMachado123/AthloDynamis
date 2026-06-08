package com.example.athlodynamis.presentation.screens.events

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.TournamentsViewModel

@Composable
fun EventsScreen(
    navController: NavController,
    userRole: AthloUserRole,
    currentUserId: String = ""
) {
    val tournamentsViewModel: TournamentsViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    val allTournaments by tournamentsViewModel.tournaments.collectAsState()
    val isLoading by tournamentsViewModel.isLoading.collectAsState()
    val error by tournamentsViewModel.error.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val isAdmin = userRole == AthloUserRole.ADMIN
    val isOrganizer = userRole == AthloUserRole.ORGANIZER
    val canCreateEvent = userRole == AthloUserRole.ADMIN || userRole == AthloUserRole.ORGANIZER

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tournamentsViewModel.loadTournaments()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun matchesSearchAndFilter(tournament: Tournament): Boolean {
        val matchesSearch =
            tournament.name.contains(searchText, ignoreCase = true) ||
                    tournament.sport.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Todos" -> true
            "Agendado" -> tournament.status == "Agendado"
            "A decorrer" -> tournament.status == "A decorrer"
            "Em preparação" -> tournament.status == "Em preparação"
            "Futebol" -> tournament.sport == "Futebol"
            "Basquetebol" -> tournament.sport == "Basquetebol"
            "Ténis" -> tournament.sport == "Ténis"
            "Voleibol" -> tournament.sport == "Voleibol"
            else -> true
        }

        return matchesSearch && matchesFilter
    }

    val filteredTournaments = allTournaments.filter { matchesSearchAndFilter(it) }

    val myTournaments = filteredTournaments.filter { tournament ->
        tournament.organizerId == currentUserId
    }

    val otherTournaments = filteredTournaments.filter { tournament ->
        tournament.organizerId != currentUserId
    }

    val headerTotal = when (userRole) {
        AthloUserRole.ORGANIZER -> myTournaments.size
        else -> filteredTournaments.size
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Events.route,
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                EventsHeader(
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    selectedFilter = selectedFilter,
                    onFilterClick = { selectedFilter = it },
                    total = headerTotal,
                    userRole = userRole
                )
            }

            if (isLoading) {
                item {
                    InfoCard(text = "A carregar torneios...")
                }
            } else if (error != null) {
                item {
                    InfoCard(text = error ?: "Erro ao carregar torneios")
                }
            } else {
                if (isOrganizer) {
                    item {
                        EventsSectionTitle(
                            title = "Os meus eventos",
                            canCreateEvent = canCreateEvent,
                            onCreateEventClick = {
                                navController.navigate(Screen.CreateEvent.route)
                            }
                        )
                    }

                    if (myTournaments.isEmpty()) {
                        item {
                            InfoCard(text = "Ainda não criaste nenhum torneio.")
                        }
                    } else {
                        items(myTournaments) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onClick = {
                                    navController.navigate(
                                        Screen.TournamentDetail.createRoute(tournament.id)
                                    )
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        EventsSectionTitle(
                            title = "Outros eventos",
                            canCreateEvent = false,
                            onCreateEventClick = {}
                        )
                    }

                    if (otherTournaments.isEmpty()) {
                        item {
                            InfoCard(text = "Não existem outros torneios para mostrar.")
                        }
                    } else {
                        items(otherTournaments) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onClick = {
                                    navController.navigate(
                                        Screen.TournamentDetail.createRoute(tournament.id)
                                    )
                                }
                            )
                        }
                    }
                } else {
                    item {
                        EventsSectionTitle(
                            title = when (userRole) {
                                AthloUserRole.ADMIN -> "Todos os eventos"
                                AthloUserRole.PLAYER -> "Eventos disponíveis"
                                else -> "Eventos"
                            },
                            canCreateEvent = canCreateEvent,
                            onCreateEventClick = {
                                navController.navigate(Screen.CreateEvent.route)
                            }
                        )
                    }

                    if (filteredTournaments.isEmpty()) {
                        item {
                            InfoCard(text = "Ainda não existem torneios para mostrar.")
                        }
                    } else {
                        items(filteredTournaments) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onClick = {
                                    navController.navigate(
                                        Screen.TournamentDetail.createRoute(tournament.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
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
private fun EventsHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterClick: (String) -> Unit,
    total: Int,
    userRole: AthloUserRole
) {
    val isAdmin = userRole == AthloUserRole.ADMIN
    val isOrganizer = userRole == AthloUserRole.ORGANIZER

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AthloColors.Navy)
                    .padding(horizontal = 22.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Eventos",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = when {
                                isAdmin -> "Vista Global - $total torneios na plataforma"
                                isOrganizer -> "$total torneios criados por ti"
                                else -> "Eventos disponíveis"
                            },
                            color = Color(0xFF8DC5F0),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (isAdmin) {
                        AdminBadge()
                    } else {
                        StatusPill(
                            text = if (isOrganizer) "$total meus" else "$total torneios",
                            background = AthloColors.SuccessBg,
                            textColor = Color(0xFF4D8B4A)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(AthloColors.DarkNavy)
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Pesquisar eventos...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = AthloColors.TextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF9CA3AF),
                        focusedContainerColor = Color(0xFF223344),
                        unfocusedContainerColor = Color(0xFF223344),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = AthloColors.TextMuted,
                        unfocusedPlaceholderColor = AthloColors.TextMuted,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                FilterRows(
                    selectedFilter = selectedFilter,
                    onFilterClick = onFilterClick
                )
            }
        }
    }
}

@Composable
private fun EventsSectionTitle(
    title: String,
    canCreateEvent: Boolean,
    onCreateEventClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (canCreateEvent) {
            Button(
                onClick = onCreateEventClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Criar Evento",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(6.dp))

                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Criar evento",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterRows(
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipPill("Todos", selectedFilter == "Todos") {
                onFilterClick("Todos")
            }

            FilterChipPill("Agendado", selectedFilter == "Agendado") {
                onFilterClick("Agendado")
            }

            FilterChipPill("A decorrer", selectedFilter == "A decorrer") {
                onFilterClick("A decorrer")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipPill("Em preparação", selectedFilter == "Em preparação") {
                onFilterClick("Em preparação")
            }

            FilterChipPill("Futebol", selectedFilter == "Futebol") {
                onFilterClick("Futebol")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipPill("Basquetebol", selectedFilter == "Basquetebol") {
                onFilterClick("Basquetebol")
            }

            FilterChipPill("Ténis", selectedFilter == "Ténis") {
                onFilterClick("Ténis")
            }

            FilterChipPill("Voleibol", selectedFilter == "Voleibol") {
                onFilterClick("Voleibol")
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) Color.White else Color(0xFF203447)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) AthloColors.TextPrimary else Color(0xFF8DC5F0),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun TournamentCard(
    tournament: Tournament,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tournament.dateRange,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tournament.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                TournamentTags(tournament = tournament)
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir evento",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun TournamentTags(tournament: Tournament) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
                text = tournament.sport,
                background = sportColor(tournament.sport),
                textColor = sportTextColor(tournament.sport)
            )

            StatusPill(
                text = tournament.status,
                background = statusColor(tournament.status),
                textColor = statusTextColor(tournament.status)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
                text = tournament.format,
                background = AthloColors.NeutralBg,
                textColor = AthloColors.TextSecondary
            )
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun AdminBadge() {
    Row(
        modifier = Modifier
            .background(Color(0xFFFFD928), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Admin",
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = "ADMIN",
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun sportColor(sport: String): Color {
    return when (sport) {
        "Futsal" -> Color(0xFFD7EBFF)
        "Futebol" -> AthloColors.SuccessBg
        "Basquetebol" -> AthloColors.WarningBg
        "Ténis" -> AthloColors.InfoBg
        "Voleibol" -> AthloColors.SuccessBg
        else -> AthloColors.InfoBg
    }
}

private fun sportTextColor(sport: String): Color {
    return when (sport) {
        "Futsal" -> AthloColors.Blue
        "Futebol" -> Color(0xFF4D8B4A)
        "Basquetebol" -> Color(0xFF9A6B22)
        "Ténis" -> AthloColors.Blue
        "Voleibol" -> Color(0xFF4D8B4A)
        else -> AthloColors.Blue
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "A decorrer" -> AthloColors.SuccessBg
        "Em preparação" -> AthloColors.WarningBg
        "Agendado" -> Color(0xFFD7EBFF)
        else -> AthloColors.NeutralBg
    }
}

private fun statusTextColor(status: String): Color {
    return when (status) {
        "A decorrer" -> Color(0xFF4D8B4A)
        "Em preparação" -> Color(0xFF9A6B22)
        "Agendado" -> AthloColors.Blue
        else -> AthloColors.TextSecondary
    }
}
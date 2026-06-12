package com.example.athlodynamis.presentation.screens.onboarding

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.athlodynamis.R
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.LanguageSwitcher
import kotlinx.coroutines.launch

data class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            titleRes = R.string.onboarding_page1_title,
            descriptionRes = R.string.onboarding_page1_desc,
            icon = Icons.Default.EmojiEvents
        ),
        OnboardingPage(
            titleRes = R.string.onboarding_page2_title,
            descriptionRes = R.string.onboarding_page2_desc,
            icon = Icons.Default.Groups
        ),
        OnboardingPage(
            titleRes = R.string.onboarding_page3_title,
            descriptionRes = R.string.onboarding_page3_desc,
            icon = Icons.Default.Timer
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AthloColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                LanguageSwitcher()
            }

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(AthloRadius.ExtraLarge),
                colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    OnboardingPageContent(page = pages[page])
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                width = if (isSelected) 24.dp else 10.dp,
                                height = 10.dp
                            )
                            .background(
                                color = if (isSelected) {
                                    AthloColors.Blue
                                } else {
                                    Color(0xFFD1D5DB)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }
                    } else {
                        onStartClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue
                ),
                shape = RoundedCornerShape(18.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 18.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.lastIndex) {
                        stringResource(R.string.onboarding_start)
                    } else {
                        stringResource(R.string.onboarding_next)
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage
) {
    val title = stringResource(page.titleRes)
    val description = stringResource(page.descriptionRes)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(AthloColors.SoftBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .background(AthloColors.DarkNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(62.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(46.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AthloColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = AthloColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}
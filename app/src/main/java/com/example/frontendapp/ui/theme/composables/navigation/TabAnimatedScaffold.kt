package com.example.frontendapp.ui.theme.composables.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TabAnimatedScaffold(
    tabs: List<TabItem>,
    modifier: Modifier = Modifier,
    ballColor: Color = Principal_variacion3,
    barColor: Color = Principal_variacion6,
    header: (@Composable () -> Unit)? = null

) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var previousTabIndex by remember { mutableIntStateOf(0) }

    val sortedTabs = tabs.sortedBy { it.index }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedNavigationBar(
                cornerRadius = shapeCornerRadius(68.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(100.dp),
                selectedIndex = selectedTabIndex,
                ballColor = ballColor,
                indentAnimation = Height(tween(400)),
                ballAnimation = Parabolic(tween(400)),
                barColor = barColor,
            ) {
                sortedTabs.forEachIndexed { i, tab ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                if (selectedTabIndex != i) {
                                    previousTabIndex = selectedTabIndex
                                    selectedTabIndex = i
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val isSelected = selectedTabIndex == i
                        val animatedBackgroundColor by animateColorAsState(
                            targetValue = if (isSelected) Principal_variacion3.copy(alpha = 0.2f) else Color.Transparent,
                            animationSpec = tween(300)
                        )
                        val animatedScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.2f else 1f,
                            animationSpec = tween(300)
                        )

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp)) // borde más suave
                                .background(animatedBackgroundColor)
                                .padding(horizontal = 16.dp, vertical = 12.dp) // más padding
                                .graphicsLayer {
                                    scaleX = animatedScale
                                    scaleY = animatedScale
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unSelectedIcon,
                                contentDescription = tab.title,
                                tint = if (isSelected) Principal_variacion3 else Color(0xFF222222)
                            )
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn(tween(300)),
                                exit = fadeOut(tween(300))
                            ) {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF111111) // color oscuro visible sobre fondo claro
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300)) togetherWith
                            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }.using(SizeTransform(clip = false))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)

        ) { targetIndex ->
            key(targetIndex) {
                sortedTabs[targetIndex].content()
            }
        }


    }
}
@Preview(showBackground = true)
@Composable
fun TabAnimatedScaffoldPreview() {
    val dummyTabs = listOf(
        TabItem(
            title = "Inicio",
            unSelectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            content = { Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) },
            index = 0
        ),
        TabItem(
            title = "Buscar",
            unSelectedIcon = Icons.Outlined.Search,
            selectedIcon = Icons.Filled.Search,
            content = { Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) },
            index = 1
        ),
        TabItem(
            title = "Perfil",
            unSelectedIcon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            content = { Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) },
            index = 2
        )
    )

    MaterialTheme {
        TabAnimatedScaffold(tabs = dummyTabs)
    }
}


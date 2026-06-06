package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ScrollToButtons(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // Smooth step distance (e.g. 350dp is approximately half a screen height, very easy for elderly to follow)
    val scrollStep = with(density) { 350.dp.toPx().toInt() }
    
    val canScrollUp = scrollState.value > 10
    val canScrollDown = scrollState.value < (scrollState.maxValue - 10) && scrollState.maxValue > 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            // Scroll Up Step Button
            AnimatedVisibility(
                visible = canScrollUp,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val targetValue = (scrollState.value - scrollStep).coerceAtLeast(0)
                            scrollState.animateScrollTo(targetValue)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Scroll ke Atas Sedikit",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Scroll Down Step Button
            AnimatedVisibility(
                visible = canScrollDown,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val targetValue = (scrollState.value + scrollStep).coerceAtMost(scrollState.maxValue)
                            scrollState.animateScrollTo(targetValue)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll ke Bawah Sedikit",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

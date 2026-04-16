package com.example.passpoint.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.passpoint.R

enum class MainEnum {
    PROFILE,
    HOME,
    SETTINGS
}

private data class BottomMenuItem(
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int,
    @param:StringRes val label: String,
    val screen: MainEnum
)

private val destinations = listOf(
    BottomMenuItem(
        icon = R.drawable.person_outline,
        selectedIcon = R.drawable.person,
        "Профиль",
        MainEnum.PROFILE
    ),
    BottomMenuItem(
        icon = R.drawable.home_outlined,
        selectedIcon = R.drawable.home,
        "Главная",
        MainEnum.HOME
    ),
    BottomMenuItem(
        icon = R.drawable.calendar_add_on_24dp,
        selectedIcon = R.drawable.calendar_add_on_24dp,
        "Моё",
        MainEnum.SETTINGS
    )
)

@Composable
fun BottomMenu(
    selectedScreen: MainEnum = MainEnum.HOME,
    onItemSelected: (MainEnum) -> Unit = {}
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 40.dp,
                    spread = 0.dp,
                    color = Color(red = 0f, green = 0f, blue = 0f, alpha = 0.25f),
                    offset = DpOffset(x = 0.dp, y = (-10).dp)
                )
            ),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        var width by remember { mutableIntStateOf(0) }

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .fillMaxWidth()
                .onSizeChanged {
                    width = it.width
                }
        ) {
            destinations.forEachIndexed { index, item ->
                val isSelected = index == selectedScreen.ordinal

                val density = LocalDensity.current

                val fullWidthDp = with(density) {
                    width.toDp()
                }

                if (fullWidthDp > (120.dp * destinations.size) + (8.dp * (destinations.size - 1))) {
                    Row(modifier = Modifier.widthIn(min = 64.dp, max = 120.dp)) {
                        Section(item, isSelected, onItemSelected)
                    }
                } else {
                    Section(item, isSelected, onItemSelected)
                }
            }
        }
    }
}


@Composable
private fun RowScope.Section(
    item: BottomMenuItem,
    isSelected: Boolean,
    onItemSelected: (MainEnum) -> Unit
) = NavigationBarItem(
    selected = isSelected,
    onClick = { onItemSelected(item.screen) },
    icon = {
        Icon(
            painter = painterResource(if (isSelected) item.selectedIcon else item.icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (!isSelected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.onErrorContainer
        )
    },
    label = {
        Text(
            item.label,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    },
    alwaysShowLabel = true,
    modifier = Modifier.height(64.dp),
    colors = NavigationBarItemDefaults.colors(
        indicatorColor = MaterialTheme.colorScheme.surfaceBright
    )
)

package com.example.passpoint.presentation.screens.main.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.passpoint.R
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.navigation.NavigationRoutes
import com.example.passpoint.presentation.theme.BrandColor

@Composable
fun AdminMainView(controller: NavHostController) {
    Column() {
        SpacerHeight(8)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Управление", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { controller.navigate(NavigationRoutes.CREATE_COURSE) },
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Создать курс",
                            modifier = Modifier.weight(1f),
                            color = BrandColor,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            contentDescription = "",
                            painter = painterResource(R.drawable.arrow_outward_24dp),
                            tint = BrandColor
                        )
                    }
                }
                Button(
                    onClick = { controller.navigate(NavigationRoutes.CREATE_EVENT) },
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Создать мероприятие",
                            modifier = Modifier.weight(1f),
                            color = BrandColor,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            contentDescription = "",
                            painter = painterResource(R.drawable.arrow_outward_24dp),
                            tint = BrandColor
                        )
                    }
                }
                Button(
                    onClick = { controller.navigate(NavigationRoutes.CREATE_NEWS) },
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Опубликовать новость",
                            modifier = Modifier.weight(1f),
                            color = BrandColor,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(
                            contentDescription = "",
                            painter = painterResource(R.drawable.arrow_outward_24dp),
                            tint = BrandColor
                        )
                    }
                }
            }
        }
    }
}
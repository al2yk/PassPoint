package com.example.passpoint.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.passpoint.R

@Composable
fun AboutProgramDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        ElevatedCard(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.orangelogo), contentDescription = "",
                    modifier = Modifier.size(60.dp)
                )
                SpacerHeight(24)
                Text(
                    "PassPoint",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center
                )
                SpacerHeight(24)
                Text(
                    "Ваш личный помощник для участия в мероприятиях, контроля посещаемости и получения сертификатов",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall
                )
                SpacerHeight(24)
                OutlinedBrandButton(
                    title = "Закрыть",
                    onClick = onDismiss
                )
            }
        }
    }
}
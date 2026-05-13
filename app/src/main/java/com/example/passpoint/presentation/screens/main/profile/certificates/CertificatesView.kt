package com.example.passpoint.presentation.screens.main.profile.certificates

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passpoint.presentation.components.SpacerHeight
import com.example.passpoint.presentation.theme.ButtonHeight
import com.example.passpoint.presentation.theme.Gray600
import com.example.passpoint.presentation.viewModel.CertificatesViewModel

@Composable
fun CertificatesView(
    controller: NavHostController?,
    innerPadding: PaddingValues,
    viewModel: CertificatesViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Box(
        modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.error != null -> {}
            state.certificates.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Сертификатов пока нет", style = MaterialTheme.typography.bodyLarge, color = Gray600) }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.certificates) { cert ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(cert.course_name, style = MaterialTheme.typography.headlineSmall)
                                SpacerHeight(8)
                                Text("Дата выдачи: ${cert.created_at?.substringBefore("T")}", style = MaterialTheme.typography.displaySmall, color = Gray600)
                                SpacerHeight(16)
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(Uri.parse(cert.certificate_url), "application/pdf")
                                            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                        }
                                        controller?.context?.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(ButtonHeight),
                                    shape = RoundedCornerShape(8.dp)
                                ) { Text("Открыть", style = MaterialTheme.typography.displaySmall) }
                            }
                        }
                    }
                }
            }
        }
    }
}
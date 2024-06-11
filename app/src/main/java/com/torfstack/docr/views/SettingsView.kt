package com.torfstack.docr.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.torfstack.docr.ui.theme.DocRTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView() {
    DocRTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Settings")
                    },
                )
            }
        ) {
            LazyVerticalGrid(modifier = Modifier.padding(it), columns = GridCells.Fixed(1)) {
                items(1) {
                    Setting(
                        title = "Biometrics",
                        description = "Enable biometric authentication",
                        onToggle = {

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Setting(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(text = title)
            Text(text = description)
        }
        Switch(
            checked = false,
            onCheckedChange = { onToggle(it) }
        )
    }
}
package com.torfstack.docr.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.torfstack.docr.ui.theme.Typography

@Composable
fun Category(text: String, thumbnail: ByteArray = byteArrayOf()) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Row {
                    Column {
                        Text(text = text, style = Typography.bodyLarge)
                        Text(text = "created today", style = Typography.bodySmall)
                    }
                }
            }
        }
    }
}
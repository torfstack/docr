package com.torfstack.docr.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.torfstack.docr.persistence.CategoryEntity
import com.torfstack.docr.ui.theme.Typography
import java.text.DateFormat

@Composable
fun Category(category: CategoryEntity) {
    val formatter = DateFormat.getDateInstance()
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
                    Image(
                        bitmap = BitmapFactory.decodeByteArray(
                            category.thumbnail,
                            0,
                            category.thumbnail.size
                        ).asImageBitmap(),
                        contentDescription = "thumbnail",
                        modifier = Modifier
                            .height(64.dp)
                            .clip(AbsoluteRoundedCornerShape(4.dp))
                            .border(1.dp, Typography.bodyLarge.color)
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(text = category.name, style = Typography.titleMedium)
                        Text(text = category.description, style = Typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Last Updated: ${formatter.format(category.lastUpdated)}",
                                style = Typography.bodySmall
                            )
                            Text(
                                text = "Created: ${formatter.format(category.created)}",
                                style = Typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
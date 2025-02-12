package com.androidsrit.baganteacafe.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.androidsrit.baganteacafe.Data.Coffee

@Composable
fun CoffeeCard(coffee: Coffee, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.padding(10.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = coffee.pictureUrl,
                    contentDescription = coffee.name,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            Text(
                text = coffee.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

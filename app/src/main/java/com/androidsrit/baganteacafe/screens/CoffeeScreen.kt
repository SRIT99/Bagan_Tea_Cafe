package com.androidsrit.baganteacafe.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.androidsrit.baganteacafe.CoffeeCard
import com.androidsrit.baganteacafe.Data.Coffee

@Composable
fun CoffeeScreen(
    coffeeList: List<Coffee>,
    onCoffeeClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
    ) {
        items(coffeeList) { coffee ->

            CoffeeCard(
                coffee = coffee,
                onClick = {
                    onCoffeeClick(coffee.id)
                }
            )

        }
    }

}

@Composable
fun CoffeeDetailsScreen(coffee: Coffee) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = coffee.pictureUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(20.dp))
        )
        Text(
            text = coffee.name,
            style = MaterialTheme.typography.titleLarge
        )
    }
}


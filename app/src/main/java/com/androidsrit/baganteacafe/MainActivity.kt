package com.androidsrit.baganteacafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.androidsrit.baganteacafe.Data.Coffee
import com.androidsrit.baganteacafe.Data.toCoffee
import com.androidsrit.baganteacafe.screens.CoffeeDetailsScreen
import com.androidsrit.baganteacafe.screens.CoffeeScreen
import com.androidsrit.baganteacafe.ui.theme.Bagan_Tea_CafeTheme
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("67aca2370017e03f14c3")
            .setSelfSigned(true)

        val databases = Databases(client)
        setContent {
            Bagan_Tea_CafeTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = CoffeeList) {
                    composable<CoffeeList> {

                        var coffeeList by remember {
                            mutableStateOf<List<Coffee>>(emptyList())
                        }
                        LaunchedEffect(key1 = true) {
                            coffeeList = getCoffeeList(databases)
                        }
                        CoffeeScreen(
                            coffeeList = coffeeList,
                            onCoffeeClick = { coffeeID ->
                                navController.navigate(CoffeeDetails(coffeeID))
                            }
                        )
                    }
                    composable<CoffeeDetails> {
                        val args = it.toRoute<CoffeeDetails>()
                        var coffee by remember {
                            mutableStateOf<Coffee?>(null)
                        }
                        LaunchedEffect(key1 = true) {
                            coffee = getCoffeeByID(args.id, databases)

                        }
                        coffee?.let {
                            CoffeeDetailsScreen(coffee = it)
                        }
                    }
                }
            }
        }
    }
}
@Serializable
data object CoffeeList

@Serializable
data class CoffeeDetails(val id:String)

@Composable
fun CoffeeCard(coffee: Coffee, onClick:() -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = coffee.pictureUrl,
                null,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .size(200.dp)
            )
            Text(
                text = coffee.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

suspend fun getCoffeeList(databases: Databases): List<Coffee> {
    val result = databases.listDocuments(
        databaseId = "coffee-db",
        collectionId = "my-coffee-list"

    )
    val coffeeList = mutableStateListOf<Coffee>()

    result.documents.forEach { document ->
        val coffee = document.data.toCoffee()
        coffeeList.add(coffee)

    }
    return coffeeList
}

suspend fun getCoffeeByID(coffeeID: String, databases: Databases): Coffee {
    val result = databases.getDocument(
        databaseId = "coffee-db",
        collectionId = "my-coffee-list",
        documentId = "coffee_$coffeeID"
    )
    return result.data.toCoffee()
}


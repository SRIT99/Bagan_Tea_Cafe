package com.androidsrit.baganteacafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.androidsrit.baganteacafe.screens.HomeUi
import com.androidsrit.baganteacafe.screens.Menu
import com.androidsrit.baganteacafe.ui.theme.Bagan_Tea_CafeTheme
import io.appwrite.Client
import io.appwrite.services.Databases
import kotlinx.serialization.Serializable


sealed class DestinationScreen(val route: String) {
    object Home : DestinationScreen("home")
    object Details : DestinationScreen("details")
    object Menu : DestinationScreen("menu")

}
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
                Scaffold(
                    topBar = {
                       BCNavigation()

                    }
                ) { innerPadding ->
                    Box(modifier= Modifier.padding(innerPadding))
                }
                    val navController1 = rememberNavController()
                    NavHost(navController = navController1, startDestination = CoffeeList) {
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
                                    navController1.navigate(CoffeeDetails(coffeeID))
                                }
                            )
                        }
                        composable<CoffeeDetails> { it ->
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


suspend fun createCoffee(coffee: Coffee, databases: Databases){
   databases.createDocument(
       databaseId = "coffee-db",
       collectionId = "my-coffee-list",
       documentId = "coffee_${coffee.id}",
       data = coffee.toJson()
   )
}
suspend fun updateCoffee(coffee: Coffee, databases: Databases){
    databases.updateDocument(
        databaseId = "coffee-db",
        collectionId = "my-coffee-list",
        documentId = "coffee_${coffee.id}",
        data = coffee.toJson()
    )
}
suspend fun deleteCoffee(coffee: Coffee, databases: Databases){
    databases.deleteDocument(
        databaseId = "coffee-db",
        collectionId = "my-coffee-list",
        documentId = "coffee_${coffee.id}"
    )
}


@Serializable
data object CoffeeList

@Serializable
data class CoffeeDetails(val id:String)


data class Coffee(
    val id: String,
    val name: String,
    val pictureUrl: String

){
    fun toJson() = mapOf(
        "id" to id,
        "name" to name,
        "pictureUrl" to pictureUrl
    )
}
fun Map<String, Any>.toCoffee() =
    Coffee(
        id = this["id"] as String,
        name = this["name"] as String,
        pictureUrl = this["pictureUrl"] as String
    )






@Composable


fun BCNavigation(){
    val navController = rememberNavController()
    NavHost(navController, startDestination = DestinationScreen.Home.route) {
        composable(DestinationScreen.Home.route) {
            HomeUi(navController)
        }
        composable(DestinationScreen.Menu.route) {
            Menu(navController)
        }
        composable(DestinationScreen.Details.route){

        }
    }
}

fun navigateTo(navController: NavController, DestinationScreen: DestinationScreen){
    navController.navigate(DestinationScreen.route)

}
@Composable
fun CoffeeCard(coffee: Coffee, onClick:() -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(10.dp,top = 60.dp)
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
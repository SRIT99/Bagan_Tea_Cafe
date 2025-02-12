package com.androidsrit.baganteacafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.androidsrit.baganteacafe.Data.Coffee
import com.androidsrit.baganteacafe.Data.toCoffee
import com.androidsrit.baganteacafe.ui.theme.Bagan_Tea_CafeTheme
import io.appwrite.Client
import io.appwrite.services.Databases

class MainActivity : ComponentActivity() {

    val client = Client(this)
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("67aca2370017e03f14c3")

    val databases = Databases(client)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bagan_Tea_CafeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    coffeehouse
                }
            }
        }
    }
}
suspend fun getCoffeeList(databases: Databases): SnapshotStateList<Coffee> {
    val result = databases.listDocuments(
        databaseId = "coffee-db",
        collectionId = "my-coffee-list"

    )
    val coffeeList = mutableStateListOf<Coffee>()

    result.documents.forEach{ document->
        val coffee = document.data.toCoffee()
        coffeeList.add(coffee)

    }
    return coffeeList
}

suspend fun getCoffeeByID(coffeeId:String,databases: Databases):Coffee{
    val result = databases.getDocument(
        databaseId = "coffee-db",
        collectionId = "my-coffee-list",
        documentId = coffeeId
    )
    return result.data.toCoffee()
}


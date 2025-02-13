package com.androidsrit.baganteacafe.Data

import io.appwrite.services.Databases
import kotlinx.serialization.Serializable

data class Coffee(
    val id: String,
    val name: String,
    val pictureUrl: String

)
fun Map<String, Any>.toCoffee() =
    Coffee(
        id = this["id"] as String,
        name = this["name"] as String,
        pictureUrl = this["pictureUrl"] as String
    )


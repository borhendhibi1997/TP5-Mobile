package com.example.tp05

import org.json.JSONObject


class Produit(jObject: JSONObject) {
    val reference: String
    val designation: String

    init {
        reference = jObject.optString("id")
        designation = jObject.optString("designation")
    }
}

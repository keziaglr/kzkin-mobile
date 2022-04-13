package edu.bluejack21_2.KZkin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Product (
    @DocumentId var id : String? = null,
    var name: String? = null,
    var brand: String? = null,
    var category: String? = null,
    var description: String? = null,
    var image: String? = null,
    var rating: Float? = 0.0f,
    var reviews: Long? = 0,
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null,
)
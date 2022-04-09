package edu.bluejack21_2.KZkin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Product (
    @DocumentId val id : String? = null,
    val name: String? = null,
    val brand: String? = null,
    val category: String? = null,
    val description: String? = null,
    val image: String? = null,
    val rating: Long? = null,
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null,
)
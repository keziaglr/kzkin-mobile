package edu.bluejack21_2.KZkin.model

import android.text.Editable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp

data class Review(
    @DocumentId var id: String? = null,
    var userId: String? = null,
    var productId: String? = null,
    var review: String? = null,
    var rating: Float = 0.0f,
    var likes: Long? = 0,
    @ServerTimestamp
    var createdAt: Timestamp? = null,
    @ServerTimestamp
    var updatedAt: Timestamp? = null,
)
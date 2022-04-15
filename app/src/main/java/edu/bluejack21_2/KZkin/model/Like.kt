package edu.bluejack21_2.KZkin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Like (
    @DocumentId var id: String? = null,
    var userId: String? = null,
    var reviewId: String? = null,
    @ServerTimestamp var createdAt: Timestamp? = null,
)
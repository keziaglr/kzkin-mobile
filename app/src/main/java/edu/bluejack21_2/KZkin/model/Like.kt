package edu.bluejack21_2.KZkin.model

import com.google.firebase.firestore.DocumentId

data class Like (
    @DocumentId var id: String? = null,
    var userId: String? = null,
    var reviewId: String? = null,
)
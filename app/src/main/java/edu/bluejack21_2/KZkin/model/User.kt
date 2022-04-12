package edu.bluejack21_2.KZkin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User (
    @DocumentId var id : String? = null,
    var name: String? = null,
    var password: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var dob: Date? = null,
    var skinType: String? = null,
    var gender: String? = null,
    var image: String? = null,
    var role: String? = null,
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null,
)
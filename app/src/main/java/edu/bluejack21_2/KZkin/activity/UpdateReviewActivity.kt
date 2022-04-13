package edu.bluejack21_2.KZkin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.model.Product
import edu.bluejack21_2.KZkin.model.Review

class UpdateReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_review)

        var rBar = findViewById<RatingBar>(R.id.ratingBarUpdate)
        var inputReview = findViewById<TextInputLayout>(R.id.inputUpdateReview)
        var btnInsert = findViewById<Button>(R.id.buttonSubmitUpdateReview)
        val db = Firebase.firestore
        val auth = Firebase.auth
        var rating: Float = 0.0f
        var reviewId = intent.extras?.getString("id").toString()

        db.collection("reviews").document(reviewId).get().addOnSuccessListener {
            var review = it.toObject(Review::class.java)
            rBar.rating = review!!.rating!!
            inputReview.editText!!.setText(review!!.review)
            var productId = review.productId

            btnInsert.setOnClickListener {
                var review = inputReview.editText!!.text
                rating = rBar.rating
                if(rating == 0.0f || review.isEmpty()){
                    Toast.makeText(this, getString(R.string.err_field_empty), Toast.LENGTH_LONG).show()
                }else{
                    val rev = Review("", auth.currentUser!!.uid, productId,
                        review.toString(), rating, 0, Timestamp.now(), Timestamp.now())
                    db.collection("reviews").document(reviewId).set(rev)
                    var product: Product? = null
                    var pReview: Long? = null
                    var pRating: Float? = null
                    var count: Int = 0
                    var sum: Float? = 0.0f
                    db.collection("reviews").whereEqualTo("productId", productId).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                var review = document.toObject(Review::class.java)
                                sum = sum?.plus(review.rating)
                                count++
                            }

                            if (productId != null) {
                                db.collection("products").document(productId).get().addOnSuccessListener {
                                    if(it != null){
                                        val newRating = sum?.div(count.toFloat())
                                        product = it.toObject(Product::class.java)
                                        pReview = product!!.reviews
                                        pRating = product!!.rating
                                        pReview = count.toLong()
                                        pRating = newRating
                                        var uProduct= Product("", product!!.name, product!!.brand, product!!.category, product!!.description,
                                            product!!.image, pRating, pReview, product!!.createdAt, Timestamp.now())

                                        if(uProduct != null){
                                            db.collection("products").document(productId).set(uProduct!!).addOnSuccessListener {
                                                Toast.makeText(this, getString(R.string.succ_submit), Toast.LENGTH_LONG).show()
                                                var intent = Intent(this, ProductDetailUserActivity::class.java)
                                                intent.putExtra("id", productId)
                                                startActivity(intent)
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }


    }
}
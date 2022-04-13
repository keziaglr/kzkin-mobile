package edu.bluejack21_2.KZkin.adapter

import android.content.Intent
import android.icu.lang.UCharacter.getAge
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.activity.ProductDetailUserActivity
import edu.bluejack21_2.KZkin.activity.UpdateReviewActivity
import edu.bluejack21_2.KZkin.model.Product
import edu.bluejack21_2.KZkin.model.Review
import edu.bluejack21_2.KZkin.model.User
import java.util.*
import kotlin.collections.ArrayList

class ReviewAdapter (private val Context: Any) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var reviewList: ArrayList<Review>? = ArrayList()
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.review_layout, viewGroup, false))
    }

    override fun getItemCount() = reviewList!!.size

    class ReviewViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviews: TextView? = itemView.findViewById(R.id.viewPosterReview)
        val ratings: TextView? = itemView.findViewById(R.id.viewReviewRating)
        val userProfile: ImageView? = itemView.findViewById(R.id.viewPosterImage)
        val userName: TextView? = itemView.findViewById(R.id.viewPosterName)
        val userAge: TextView? = itemView.findViewById(R.id.viewPosterAge)
        val likes: TextView? = itemView.findViewById(R.id.viewReviewLike)
        val editBtn : ImageButton = itemView.findViewById(R.id.btnEditReview)

        fun binding(review: Review){
            reviews?.setText(review.review.toString())
            ratings?.setText(review.rating.toString())
            likes?.setText(review.likes.toString())

            val db = Firebase.firestore
            db.collection("users").document(review!!.userId.toString()).get().addOnSuccessListener {
                    document->
                var user = document.toObject(User::class.java)
                Log.e("USER", review!!.userId!!)
                userName?.setText(user!!.name)

                val requestOption = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

                if (user!!.image != null) {
                    Glide.with(itemView.context)
                        .applyDefaultRequestOptions(requestOption)
                        .load(user!!.image)
                        .into(userProfile!!)
                }

                if(user!!.dob != null){
                    val monthNumber = DateFormat.format("MM", user!!.dob)
                    val yearNumber = DateFormat.format("yyyy", user!!.dob)
                    val dayNumber = DateFormat.format("dd", user!!.dob)
                    var age =  getAge(Integer.parseInt(yearNumber.toString()) , Integer.parseInt(monthNumber.toString()), Integer.parseInt(dayNumber.toString()))
                    userAge?.setText(age.toString() + " years old, " + user!!.skinType)
                }else{
                    userAge?.setText("unknown")
                }
            }


        }
        fun getAge(year: Int, month: Int, day: Int): Int? {
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()
            dob[year, month] = day
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            Log.e("NOW", today[Calendar.YEAR].toString())
            Log.e("BDAY ", dob[Calendar.YEAR].toString())
            Log.e("selisih", age.toString())
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            val ageInt = age
            return ageInt
        }
    }



    fun submitList(pList: ArrayList<Review>){
        reviewList = pList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val newList = reviewList?.get(position)

        when(holder) {
            is ReviewViewHolder -> {
                holder.binding(reviewList!!.get(position))
                holder.editBtn.setOnClickListener{
                    var intent = Intent(it.context, UpdateReviewActivity::class.java)
                    intent.putExtra("id", reviewList!!.get(position).id)
                    it.context.startActivities(arrayOf(intent))
                }
            }
        }
    }
}
package edu.bluejack21_2.KZkin.adapter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.activity.ProductDetailUserActivity
import edu.bluejack21_2.KZkin.activity.UpdateReviewActivity
import edu.bluejack21_2.KZkin.model.Like
import edu.bluejack21_2.KZkin.model.Product
import edu.bluejack21_2.KZkin.model.Review
import edu.bluejack21_2.KZkin.model.User
import java.util.*


class ReviewAdapter (private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        val deleteBtn: ImageButton = itemView.findViewById(R.id.btnDeleteReview)
        val favBtn: ImageButton = itemView.findViewById(R.id.btnLikeReview)
        val numLikes: TextView = itemView.findViewById(R.id.viewReviewLike)



        fun binding(review: Review){
            reviews?.setText(review.review.toString())
            ratings?.setText(review.rating.toString())
            likes?.setText(review.likes.toString())
            Log.e("REVIEW ADAPTER", review.review.toString())

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
                    var years = itemView.context.getResources().getString(R.string.old)
                    var skin = ""
                    if(years.equals("tahun")){
                        if(user!!.skinType.equals("Oily Skin")){
                            skin = "Kulit Berminyak"
                        }else if(user!!.skinType.equals("Combination Skin")){
                            skin = "Kulit Kombinasi"
                        }else if(user!!.skinType.equals("Dry Skin")){
                            skin = "Kulit Kering"
                        }else if(user!!.skinType.equals("Normal Skin")){
                            skin = "Kulit Normal"
                        }
                    }else{
                        skin = user!!.skinType.toString()
                    }
                    userAge?.setText(age.toString() + " " + years + ", " + skin)
                }else{
                    userAge?.setText("unknown")
                }
                var counter = 0
                val db = Firebase.firestore
                db.collection("likes").whereEqualTo("reviewId", review.id).get().addOnCompleteListener {task->
                    if(task.isSuccessful){
                        for (document in task.result) {
                            Log.e("DATA", document.data.toString())
                            counter++
                        }
                        db.collection("reviews").document(review.id.toString()).set(Review(review.id, review.userId, review.productId, review.review, review.rating, counter.toLong(), review.createdAt, Timestamp.now()))
                        numLikes.setText(counter.toString())
                    }
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
        val db = Firebase.firestore
        val auth = Firebase.auth

        when(holder) {
            is ReviewViewHolder -> {
                holder.binding(reviewList!!.get(position))

                db.collection("reviews").document(reviewList!!.get(position).id.toString()).get().addOnSuccessListener {
                    var rev = it.toObject(Review::class.java)
                    if(rev!!.userId == auth.currentUser!!.uid){
                        Log.e("USER PROD ADAPT", "${rev!!.userId} ${auth.currentUser!!.uid}")
                        holder.editBtn.visibility = View.VISIBLE
                        holder.deleteBtn.visibility = View.VISIBLE
                        holder.editBtn.setOnClickListener{
                            var intent = Intent(it.context, UpdateReviewActivity::class.java)
                            intent.putExtra("id", reviewList!!.get(position).id)
                            it.context.startActivities(arrayOf(intent))
                        }
                        holder.deleteBtn.setOnClickListener{ view->

                            reviewList!!.get(position).id?.let { it1 ->
                                db.collection("reviews").document(
                                    it1
                                ).delete().addOnSuccessListener { aa->
                                    var product: Product? = null
                                    var pReview: Long? = null
                                    var pRating: Float? = null
                                    var count: Float = 0.0f
                                    var sum: Float? = 0.0f
                                    var productId = reviewList!!.get(position).productId
                                    Log.e("YESYY", "MASUK")
                                    db.collection("reviews").whereEqualTo("productId", productId).get().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.e("TASK", "masuk task")
                                            for (document in task.result) {
                                                var review = document.toObject(Review::class.java)
                                                sum = sum?.plus(review.rating)
                                                Log.e("SUM", "${sum} ${count}")
                                                count++
                                            }
                                            Log.e("FINAL SUM", "${sum} ${count}")

                                            if (productId != null) {
                                                db.collection("products").document(productId).get().addOnSuccessListener {
                                                    if(it != null){
                                                        val newRating = sum!!.toFloat()?.div(count.toFloat())
                                                        product = it.toObject(Product::class.java)
                                                        pReview = count.toLong()

                                                        if(count == 0.0f){
                                                            pRating = 0.0f
                                                        }else{
                                                            pRating = newRating
                                                        }
                                                        var uProduct= Product("", product!!.name, product!!.brand, product!!.category, product!!.description,
                                                            product!!.image, pRating, pReview, product!!.createdAt, Timestamp.now())

                                                        if(uProduct != null){
                                                            Log.e("SET PRODUCT", "${pReview} ${pRating}")

                                                            db.collection("products").document(productId).set(uProduct!!)
                                                            if(context is ProductDetailUserActivity){
                                                                var ctx = context as ProductDetailUserActivity
                                                                ctx.setRating(pRating!!.toFloat())
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Toast.makeText(view.context, R.string.succ_delete, Toast.LENGTH_SHORT).show()
                                    reviewList!!.removeAt(position)
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, reviewList!!.size);
                                }

                            }
                        }

                    }
                }

                db.collection("likes").whereEqualTo("userId", auth.currentUser!!.uid).whereEqualTo("reviewId", reviewList!!.get(position).id).get().addOnCompleteListener { task->

                    if (task.isSuccessful){
                        if(task.result.isEmpty){
                            holder.favBtn.setImageResource(R.drawable.ic_unfavorite);
                        }else{
                            holder.favBtn.setImageResource(R.drawable.ic_favorite);
                        }
                    }
                    var counter = 0
                    holder.favBtn.setOnClickListener {
                        if (task.isSuccessful){
                            if(task.result.isEmpty){
                                holder.favBtn.setImageResource(R.drawable.ic_favorite);
                                notifyDataSetChanged()
                                var like = Like("", auth.currentUser!!.uid, reviewList!!.get(position).id, Timestamp.now())
                                db.collection("likes").add(like)
                                var builder : Notification.Builder
                                var notificationManager : NotificationManager
                                var notificationChannel : NotificationChannel
                                val channelId = "edu.bluejack21_2.KZkin"
                                var description = "Test"
                                val context: Context = holder.itemView.getContext()

                                notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                                db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
                                    var user = it.toObject(User::class.java)
                                    if(user != null){
                                        if(reviewList!!.get(position).userId != auth.currentUser!!.uid){
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                notificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                                                notificationChannel.enableLights(true)
                                                notificationChannel.lightColor = Color.GREEN
                                                notificationChannel.enableVibration(false)
                                                notificationManager.createNotificationChannel(notificationChannel)

                                                builder = Notification.Builder(context,channelId)
                                                    .setContentTitle("KZkin Notification")
                                                    .setContentText(user.name + " liked your review")
                                                    .setSmallIcon(R.mipmap.ic_logo_foreground)
                                                    .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.mipmap.ic_logo_foreground))
//                                .setContentIntent(pendingIntent)
                                            }else{

                                                builder = Notification.Builder(context)
                                                    .setContentTitle("KZkin Notification")
                                                    .setContentText(user.name + " liked your review")
                                                    .setSmallIcon(R.mipmap.ic_logo_foreground)
                                                    .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.mipmap.ic_logo_foreground))
//                                .setContentIntent(pendingIntent)
                                            }
                                            notificationManager.notify(1234,builder.build())
                                        }
                                    }
                                }

                            }else{
                                val like = task.result.documents[0]

                                like.toObject(Like::class.java)
                                holder.favBtn.setImageResource(R.drawable.ic_unfavorite)

                                db.collection("likes").document(like.id).delete()
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
                    .addOnFailureListener {
                        Log.e("fave", "FAIL")
                    }
            }
        }
    }
}
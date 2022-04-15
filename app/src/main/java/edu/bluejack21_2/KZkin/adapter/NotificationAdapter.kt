package edu.bluejack21_2.KZkin.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.activity.ProductDetailAdminActivity
import edu.bluejack21_2.KZkin.activity.ProductDetailUserActivity
import edu.bluejack21_2.KZkin.model.Like
import edu.bluejack21_2.KZkin.model.Product
import edu.bluejack21_2.KZkin.model.User

class NotificationAdapter (private val Context: Any) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var likeList: ArrayList<Like>? = ArrayList()


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LikeViewHolder {
        return LikeViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.notification_layout, viewGroup, false))
    }

    override fun getItemCount() = likeList!!.size

    class LikeViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val likerImage: ImageView? = itemView.findViewById(R.id.viewLikerImage)
        val likerName: TextView? = itemView.findViewById(R.id.viewLikerName)
        val db = Firebase.firestore
        val auth = Firebase.auth

        fun binding(like: Like){
            db.collection("users").document(like.userId.toString()).get().addOnSuccessListener {
                var user = it.toObject(User::class.java)
                if(user != null){
                    likerName!!.setText(user.name)
                    val requestOption = RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)

                    if (likerImage != null && user.image != null) {
                        Glide.with(itemView.context)
                            .applyDefaultRequestOptions(requestOption)
                            .load(user.image)
                            .into(likerImage)
                }

            }

            }

        }
    }

    fun submitList(pList: ArrayList<Like>){
        likeList = pList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val newList = likeList?.get(position)

        when(holder) {
            is LikeViewHolder -> {
                holder.binding(likeList!!.get(position))
            }
        }
    }
}
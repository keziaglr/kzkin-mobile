package edu.bluejack21_2.KZkin.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.adapter.NotificationAdapter
import edu.bluejack21_2.KZkin.model.Like
import edu.bluejack21_2.KZkin.model.Review

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var notificationRV: RecyclerView? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationArrayList: ArrayList<Like>? = null
    private var tempList: ArrayList<Like>? = ArrayList()
    private var reviewList: ArrayList<Review> = ArrayList()
    var linearLayoutManager: LinearLayoutManager? = null
    val db = Firebase.firestore
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.collection("reviews").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (rev in it.result) {
                    reviewList.add(rev.toObject(Review::class.java))
                }
            }
        }

        notificationRV = view.findViewById<RecyclerView>(R.id.viewAllNotificationHomeRV)
        notificationArrayList = ArrayList<Like>()
        notificationAdapter = NotificationAdapter(this)

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        notificationRV!!.setLayoutManager(linearLayoutManager)
        notificationRV!!.setAdapter(notificationAdapter)
        notificationRV!!.adapter = notificationAdapter
        notificationAdapter!!.notifyDataSetChanged()


        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshProduct)
        swipeRefresh.setOnRefreshListener {
            getNotifications()

            notificationRV!!.adapter = notificationAdapter
            notificationAdapter!!.notifyDataSetChanged()

            swipeRefresh.isRefreshing = false
        }
    }

    private fun getNotifications() {
        db.collection("likes").orderBy("createdAt", Query.Direction.DESCENDING).limit(3).get()
            .addOnSuccessListener { result ->
                notificationArrayList!!.clear()
                tempList!!.clear()
                for (document in result) {
                    val like = document.toObject(Like::class.java)
                    if (checkReview(like.reviewId.toString())) {
                        notificationArrayList!!.add(like)
                    }
                }
                if (notificationArrayList!!.size > 10) {
                    for (i in 0 until 10) {
                        tempList!!.add(notificationArrayList!!.get(i))
                    }
                } else {
                    for (i in 0 until notificationArrayList!!.size) {
                        tempList!!.add(notificationArrayList!!.get(i))
                    }
                }

                notificationAdapter!!.submitList(tempList!!)
            }
            .addOnFailureListener { exception ->
                Log.d("hi", "Error getting documents: ", exception)
            }
    }

    private fun checkReview(reviewId: String): Boolean {
        for (rev in reviewList) {
            if (rev!!.userId == auth.currentUser!!.uid && rev!!.id == reviewId) {
                return true
            }
        }
        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
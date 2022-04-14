package edu.bluejack21_2.KZkin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.adapter.ProductAdapter
import edu.bluejack21_2.KZkin.adapter.ReviewAdapter
import edu.bluejack21_2.KZkin.model.Product
import edu.bluejack21_2.KZkin.model.Review
import edu.bluejack21_2.KZkin.model.User
import android.text.format.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailUserActivity : AppCompatActivity(){
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var reviewRV: RecyclerView? = null
    private var reviewAdapter: ReviewAdapter? = null
    private var reviewList: ArrayList<Review>? = null
    private var tempList: ArrayList<Review> = ArrayList()
    private var userList: ArrayList<User> = ArrayList()
    private var index = 5
    private var id = ""
    var linearLayoutManager : LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail_user)

        id = intent.extras?.getString("id").toString()


        reviewRV = findViewById<RecyclerView>(R.id.viewReviewRV)
        reviewList = ArrayList<Review>()
        reviewAdapter = ReviewAdapter(this)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reviewRV!!.setLayoutManager(linearLayoutManager)
        reviewRV!!.setAdapter(reviewAdapter)
        reviewRV!!.adapter = reviewAdapter
        reviewAdapter!!.notifyDataSetChanged()

        refreshPage()

        db.collection("users").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (document in it.result){
                    userList.add(document.toObject(User::class.java))
                }
            }
        }

        if (id != null ) {
            db.collection("products").document(id).get()
                .addOnSuccessListener {document ->
                    var product = document.toObject(Product::class.java)

                    var image = findViewById<ImageView>(R.id.imgProductDetail)
                    var brand = findViewById<TextView>(R.id.textProductDetailBrand)
                    var name = findViewById<TextView>(R.id.textProductDetailName)
                    var desc = findViewById<TextView>(R.id.textProductDetailDesc)

                    val requestOption = RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)

                    Glide.with(this)
                        .applyDefaultRequestOptions(requestOption)
                        .load(product!!.image)
                        .into(image)
                    brand.setText(product!!.brand)
                    name.setText(product!!.name)
                    desc.setText(product!!.description)
                }

        }

        var btnInsertReview = findViewById<Button>(R.id.buttonInsertReview)
        db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
            var user = it.toObject(User::class.java)
            btnInsertReview.setOnClickListener{
                if(user!!.dob == null || user.skinType == null){
                    Toast.makeText(this, getString(R.string.err_update_profile), Toast.LENGTH_LONG).show()
                }else{
                    var intent = Intent(this, InsertReviewActivity::class.java)
                    intent.putExtra("id", id)
                    startActivity(intent)
                }
            }
        }

        var btnFilter = findViewById<ImageButton>(R.id.buttonFilterReview)
        btnFilter.setOnClickListener {
            var intent = Intent(this, FilterReviewActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }


//        setTab()
    }

    private fun refreshPage() {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshReview)
        swipeRefresh.setOnRefreshListener {
            getALlReview()

            reviewRV!!.adapter = reviewAdapter
            reviewAdapter!!.notifyDataSetChanged()

            swipeRefresh.isRefreshing = false
        }

        reviewRV!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.e("TEST", "${dy} ${reviewAdapter!!.itemCount}")
                if (dy > 0) {
                    var vItem = linearLayoutManager!!.childCount
                    var lItem = linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    var count = reviewAdapter!!.itemCount
                    Log.e("TEST", "${vItem} ${lItem} ${count}")
                    if((vItem + lItem) == count){
                        Log.e("INDEX", index.toString())
                        if (index < reviewList!!.size){
                            index++
                        }
                        getALlReview()
                    }

                }
            }
        })
    }


    private fun getAge(year: Int, month: Int, day: Int): Int? {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt
    }

    private fun getALlReview(){
        var temp : Query = db.collection("reviews").whereEqualTo("productId", id)

        if(intent.extras!!.getString("fRating") != null && intent.extras!!.getString("fRating")!!.isEmpty() == false){
            temp = temp.whereEqualTo("rating", intent.extras!!.getString("fRating")!!.toFloat())
        }

        if(intent.extras!!.getString("fLike") != null && intent.extras!!.getString("fLike") == "true"){
            temp = temp.orderBy("like", Query.Direction.DESCENDING)
        }

        temp.get()
            .addOnSuccessListener { result ->
                reviewList!!.clear()
                tempList.clear()
                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    if(intent.extras!!.getString("fAge") != null && intent.extras!!.getString("fSkin") != null && intent.extras!!.getString("fAge")!!.isEmpty() == false && intent.extras!!.getString("fSkin")!!.isEmpty() == false) {
                        Log.e("FB", "AGE SKIN")
                        if (checkAge(review.userId!!) && checkSkin(review.userId!!)) {
                            reviewList!!.add(review)
                        }
                    }else if(intent.extras!!.getString("fAge") != null && intent.extras!!.getString("fAge")!!.isEmpty() == false){
                        Log.e("FB", "AGE")
                        if (checkAge(review.userId!!)) {
                            reviewList!!.add(review)
                        }
                    }else if(intent.extras!!.getString("fSkin") != null && intent.extras!!.getString("fSkin")!!.isEmpty() == false){
                        Log.e("FB", "SKIN")
                        if (checkSkin(review.userId!!)) {
                            reviewList!!.add(review)
                        }
                    }else{
                        reviewList!!.add(review)
                    }
                }
                Log.e("Review Adapter dipanbgggil", "this")
                if(reviewList!!.size >= 5){
                    for (i in 0 until index){
                        tempList.add(reviewList!!.get(i))
                    }
                }else{
                    for(review in reviewList!!){
                        tempList.add(review)
                    }
                }

                Handler().postDelayed({
                    reviewAdapter!!.submitList(tempList!!)
                }, 5000)
            }
            .addOnFailureListener { exception ->
                Log.d("hi", "Error getting documents: ", exception) }
    }

    fun checkAge(userId: String) : Boolean{
        for (user in userList){
            val monthNumber = DateFormat.format("MM", user!!.dob)
            val yearNumber = DateFormat.format("yyyy", user!!.dob)
            val dayNumber = DateFormat.format("dd", user!!.dob)
            var uAge = getAge(Integer.parseInt(yearNumber.toString()) , Integer.parseInt(monthNumber.toString()), Integer.parseInt(dayNumber.toString()))
            var fAge = intent.extras!!.getString("fAge")
            if(uAge == fAge!!.toInt()){
                return true
            }
        }
        return false
    }

    fun checkSkin(userId: String) : Boolean{
        for (user in userList){
            Log.e("FB", "${user!!.skinType.toString()} ${intent.extras!!.getString("fSkin").toString()}")
            if(intent.extras!!.getString("fSkin").toString() == user!!.skinType.toString()){
                Log.e("FB TRUE", user!!.skinType.toString())
                return true
            }
        }
        return false
    }


//    fun setTab(){
//        val adapter = ProductDetailAdapter(supportFragmentManager)
//        adapter.addFragment(ProductInformationUserFragment() , getString(R.string.tab_detail))
//        adapter.addFragment(ProductReviewFragment() , getString(R.string.tab_review))
//        val viewPager = findViewById<ViewPager>(R.id.view_pager_user)
//        viewPager.adapter = adapter
//        val tabs = findViewById<TabLayout>(R.id.tab_user)
//        tabs.setupWithViewPager(viewPager)
//    }

//    override fun passDataCom(id: String) {
//        val bundle = Bundle()
//        bundle.putString("ids" , id)
//
//        val transaction = this.supportFragmentManager.beginTransaction()
//
//        val fragmentService = ProductInformationUserFragment()
//        fragmentService.arguments = bundle
//
//        transaction.replace(R.id.view_pager_user, fragmentService)
//        transaction.commit()
//
//    }
}
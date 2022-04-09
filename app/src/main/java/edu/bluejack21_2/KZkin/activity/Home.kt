package edu.bluejack21_2.KZkin.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.adapter.ProductAdapter
import edu.bluejack21_2.KZkin.model.Product


class Home : AppCompatActivity() {
    private var productRV: RecyclerView? = null
    private var productAdapter: ProductAdapter? = null
    private var productArrayList: ArrayList<Product>? = null
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        productRV = findViewById<RecyclerView>(R.id.viewProductHomeRV)
        productArrayList = ArrayList<Product>()
        productAdapter = ProductAdapter(this, productArrayList!!)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        productRV!!.setLayoutManager(linearLayoutManager)
        productRV!!.setAdapter(productAdapter)
        productRV!!.adapter = productAdapter
        productAdapter!!.notifyDataSetChanged()
        refreshPage()
    }

    private fun refreshPage() {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshProduct)
        swipeRefresh.setOnRefreshListener {

            getALlProduct()

            productRV!!.adapter = productAdapter
            productAdapter!!.notifyDataSetChanged()

            swipeRefresh.isRefreshing = false
        }
    }

    private fun getALlProduct(){
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productArrayList!!.clear()
                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    productArrayList!!.add(product)
                    Log.d("hi", "${productArrayList!!.size}")
                    Log.d("hi", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("hi", "Error getting documents: ", exception) }
    }
}
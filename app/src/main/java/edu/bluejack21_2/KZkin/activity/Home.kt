package edu.bluejack21_2.KZkin.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AbsListView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.adapter.ProductAdapter
import edu.bluejack21_2.KZkin.model.Product


class Home : AppCompatActivity() {
    private var productRV: RecyclerView? = null
    private var hottestProductRV: RecyclerView? = null
    private var productAdapter: ProductAdapter? = null
    private var productAdapter1: ProductAdapter? = null
    private var productArrayList: ArrayList<Product>? = null
    private var tempList: ArrayList<Product>? = null
    private var hottestProductArrayList: ArrayList<Product>? = null
    private var sort = -1
    private var filter : String = ""
    private var index = 5
    var linearLayoutManager : LinearLayoutManager? = null
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        productRV = findViewById<RecyclerView>(R.id.viewAllProductsHomeRV)
        hottestProductRV = findViewById<RecyclerView>(R.id.viewHottestProductsHomeRV)
        productArrayList = ArrayList<Product>()
        tempList = ArrayList<Product>()
        hottestProductArrayList = ArrayList()
        productAdapter = ProductAdapter(this)
        productAdapter1 = ProductAdapter(this)
/*        getALlProduct()
        getHottestProducts()*/
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        productRV!!.setLayoutManager(linearLayoutManager)
        productRV!!.setAdapter(productAdapter)
        productRV!!.adapter = productAdapter
        productAdapter!!.notifyDataSetChanged()

        val linearLayoutManager1 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        hottestProductRV!!.setLayoutManager(linearLayoutManager1)
        hottestProductRV!!.setAdapter(productAdapter1)
        hottestProductRV!!.adapter = productAdapter1
        productAdapter1!!.notifyDataSetChanged()
        refreshPage()

        val sortItems = resources.getStringArray(R.array.product_sort)
        val buttonSortHome: Button? = findViewById<Button>(R.id.buttonSortProductsHome)
        buttonSortHome!!.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.sort))
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    sort = -1
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    getALlProduct()
                }
                .setSingleChoiceItems(sortItems, sort) { dialog, which ->
                    sort = which
                }
                .show()
        }

        val filterItems = resources.getStringArray(R.array.product_categories)
        val buttonFilterHome: Button? = findViewById<Button>(R.id.buttonFilterProductsHome)
        var filterIdx = -1
        buttonFilterHome!!.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.filter))
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    filter = ""
                    filterIdx = -1
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    getALlProduct()
                }
                .setSingleChoiceItems(filterItems, filterIdx) { dialog, which ->
                    filterIdx = which
                    if(which == 0){
                        filter = "Skincare"
                    }else if(which == 1){
                        filter = "Make Up"
                    }else if(which == 2){
                        filter = "Haircare"
                    }else if(which == 3){
                        filter = "Fragrances"
                    }
                }
                .show()
        }

    }

    private fun refreshPage() {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshProduct)
        swipeRefresh.setOnRefreshListener {
            getALlProduct()
            getHottestProducts()

            productRV!!.adapter = productAdapter
            productAdapter!!.notifyDataSetChanged()

            hottestProductRV!!.adapter = productAdapter1
            productAdapter1!!.notifyDataSetChanged()

            swipeRefresh.isRefreshing = false
        }

        productRV!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.e("dy", dy.toString())
                if (dy > 0) {
                    var vItem = linearLayoutManager!!.childCount
                    var lItem = linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    var count = productAdapter!!.itemCount
                    Log.e("TEST", "${vItem} ${lItem} ${count}")
                    if((vItem + lItem) == count){

                        Log.e("INDEX", index.toString())
                        if (index <= productArrayList!!.size){
                            index++
                        }
                        getALlProduct()
                    }

                }
            }
        })
    }

//    private fun addMoreProducts(){
//        productArrayList!!.clear()
//        for(i in productArrayList!!.size..tempList!!.size){
//            productArrayList!!.add(tempList!!.get(i))
//        }
//        Handler().postDelayed({
//            productAdapter!!.submitList(productArrayList!!)
//        }, 5000)
//    }

    private fun getALlProduct(){
        var temp : Query = db.collection("products")

        if(!filter.equals("")){
            temp = temp.whereEqualTo("category", filter)
        }

        if (sort != -1){
            if (sort == 0){
                temp = temp.orderBy("createdAt", Query.Direction.ASCENDING)
            }else if (sort == 1){
                temp = temp.orderBy("createdAt", Query.Direction.DESCENDING)
            }else if (sort == 2){
                temp = temp.orderBy("rating", Query.Direction.DESCENDING)
            }else if (sort == 3){
                temp = temp.orderBy("rating", Query.Direction.ASCENDING)
            }
        }

        temp.limit(index.toLong()).get()
        .addOnSuccessListener { result ->
            productArrayList!!.clear()
            for (document in result) {
                val product = document.toObject(Product::class.java)
                productArrayList!!.add(product)
            }

            Handler().postDelayed({
                productAdapter!!.submitList(productArrayList!!)
            }, 5000)
        }
        .addOnFailureListener { exception ->
            Log.d("hi", "Error getting documents: ", exception) }
    }

    private fun getHottestProducts(){
        db.collection("products").orderBy("reviews", Query.Direction.DESCENDING).limit(3).get()
            .addOnSuccessListener { result ->
                hottestProductArrayList!!.clear()
                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    hottestProductArrayList!!.add(product)
                }
                productAdapter1!!.submitList(hottestProductArrayList!!)
            }
            .addOnFailureListener { exception ->
                Log.d("hi", "Error getting documents: ", exception) }
    }
}
package edu.bluejack21_2.KZkin.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productRV = requireView().findViewById<RecyclerView>(R.id.viewAllProductsHomeRV)
        hottestProductRV = requireView().findViewById<RecyclerView>(R.id.viewHottestProductsHomeRV)
        productArrayList = ArrayList<Product>()
        tempList = ArrayList<Product>()
        hottestProductArrayList = ArrayList()
        productAdapter = ProductAdapter(this)
        productAdapter1 = ProductAdapter(this)
       getALlProduct()
        getHottestProducts()
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        productRV!!.setLayoutManager(linearLayoutManager)
        productRV!!.setAdapter(productAdapter)
        productRV!!.adapter = productAdapter
        productAdapter!!.notifyDataSetChanged()

        val linearLayoutManager1 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        hottestProductRV!!.setLayoutManager(linearLayoutManager1)
        hottestProductRV!!.setAdapter(productAdapter1)
        hottestProductRV!!.adapter = productAdapter1
        productAdapter1!!.notifyDataSetChanged()
        refreshPage()

        val sortItems = resources.getStringArray(R.array.product_sort)
        val buttonSortHome: Button? = requireView().findViewById<Button>(R.id.buttonSortProductsHome)
        buttonSortHome!!.setOnClickListener {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
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
        }

        val filterItems = resources.getStringArray(R.array.product_categories)
        val buttonFilterHome: Button? = requireView().findViewById<Button>(R.id.buttonFilterProductsHome)
        var filterIdx = -1
        buttonFilterHome!!.setOnClickListener {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
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

    }

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

//                if(productArrayList!!.size >= 5){
//                    for (i in tempList!!.size until index){
//                        tempList!!.add(productArrayList!!.get(i))
//                    }
//                }else{
//                    for(product in productArrayList!!){
//                        tempList!!.add(product)
//                    }
//                }

                Handler().postDelayed({
                    productAdapter!!.submitList(productArrayList!!)
                }, 7000)
            }
            .addOnFailureListener { exception ->
                Log.d("hi", "Error getting documents: ", exception) }
    }

    private fun getHottestProducts(){
        db.collection("products").whereGreaterThan("reviews", 0).orderBy("reviews", Query.Direction.DESCENDING).limit(3).get()
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

    private fun refreshPage() {
        val swipeRefresh =
            requireView().findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshProduct)
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
                    if ((vItem + lItem) == count) {

                        Log.e("INDEX", index.toString())
                        Log.e("CHILD", productAdapter!!.itemCount.toString())
                        if (index <= productAdapter!!.itemCount) {
                            index++
                        }
                        getALlProduct()
                    }

                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
package edu.bluejack21_2.KZkin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.model.Product

class ProductDetailAdminActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail_admin)
        id = intent.extras?.getString("id").toString()
        if (id != null ) {
            db.collection("products").document(id).get()
                .addOnSuccessListener {document ->

                    var product = document.toObject(Product::class.java)
                    if(product != null){
                        var image = findViewById<ImageView>(R.id.imgProductDetail2)
                        var brand = findViewById<TextView>(R.id.textProductDetailBrand2)
                        var name = findViewById<TextView>(R.id.textProductDetailName2)
                        var desc = findViewById<TextView>(R.id.textProductDetailDesc2)

                        val requestOption = RequestOptions()
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)

                        if(product!!.image != null){
                            Glide.with(this)
                                .applyDefaultRequestOptions(requestOption)
                                .load(product!!.image)
                                .into(image)
                        }

                        brand.setText(product!!.brand)
                        name.setText(product!!.name)
                        desc.setText(product!!.description)
                    }

                }

        }

        var btnUpdateProduct = findViewById<Button>(R.id.buttonUpdateProduct)

        btnUpdateProduct.setOnClickListener {
            var intent = Intent(this, UpdateProductActivity::class.java)
            intent.putExtra("id", id.toString())
            startActivity(intent)

        }
    }
}
package edu.bluejack21_2.KZkin.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.model.Product
import java.io.IOException
import java.util.*

private val btnSelect: Button? = null
private  var btnUpload:android.widget.Button? = null
private var filePath: Uri? = null
private const val PICK_IMAGE_REQUEST = 1234
private var storage: FirebaseStorage? = null
private var storageReference: StorageReference? = null
private lateinit var productPicture: String
private var photoProduct: String? = ""

class InsertProduct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_product)
        val db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val categories = resources.getStringArray(R.array.product_categories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, categories)
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteProductCategoryTextView)
        autocompleteTV.setAdapter(arrayAdapter)

        val buttonSubmit = findViewById<Button>(R.id.buttonSubmitInsertProduct)
        val inputProductName = findViewById<TextInputLayout>(R.id.inputInsertProductName)
        val inputProductBrand = findViewById<TextInputLayout>(R.id.inputInsertProductBrand)
        val inputProductCategory = findViewById<TextInputLayout>(R.id.inputInsertProductCategory)
        val inputProductDescription = findViewById<TextInputLayout>(R.id.inputInsertProductDescription)
        val buttonProductImage = findViewById<Button>(R.id.buttonInsertProductImage)

        buttonProductImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_image)), PICK_IMAGE_REQUEST)
        }

        buttonSubmit.setOnClickListener {

            val productName = inputProductName.editText!!.text.toString()
            val productBrand = inputProductBrand.editText!!.text.toString()
            val productCategory = inputProductCategory.editText!!.text.toString()
            val productDescription = inputProductDescription.editText!!.text.toString()

            if (productName.isEmpty() || productBrand.isEmpty() || productCategory.isEmpty() || photoProduct!!.isEmpty()){
                Toast.makeText(this, getString(R.string.err_field_empty), Toast.LENGTH_LONG).show()
            }else if (productDescription.length < 10){
                Toast.makeText(this, getString(R.string.err_desc), Toast.LENGTH_LONG).show()
            }else{
                val product = Product("", productName, productBrand, productCategory, productDescription,
                    photoProduct!!, 0.0f, 0, Timestamp.now(), Timestamp.now())
                db.collection("products").add(product)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(applicationContext, getString(R.string.succ_submit), Toast.LENGTH_SHORT).show()
//                        val goToNextActivity = Intent(
//                            applicationContext,
//                            Home::class.java
//                        )
//                        startActivity(goToNextActivity)
                    }
                    .addOnFailureListener { e ->
                        Log.w("hi", "Error adding document", e)
                    }
            }

        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data?.data!!
            try {
                uploadFile().toString()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadFile() {
        if (filePath != null) {
            val imageRef = storageReference!!.child("product/" + UUID.randomUUID().toString() )
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        photoProduct = it.toString()
                    }

                    Toast.makeText(applicationContext, getString(R.string.succ_img), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.err_img), Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapShot ->
                    val progress =
                        100.00 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount

                }
        }
    }
}
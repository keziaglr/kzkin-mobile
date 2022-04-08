package edu.bluejack21_2.KZkin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.OnProgressListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.model.Product


class InsertProduct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_product)
        val db = Firebase.firestore

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
            startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), 1234)
        }

        buttonSubmit.setOnClickListener {

            val productName = inputProductName.editText!!.text.toString()
            val productBrand = inputProductBrand.editText!!.text.toString()
            val productCategory = inputProductCategory.editText!!.text.toString()
            val productDescription = inputProductDescription.editText!!.text.toString()

            if (productName.isEmpty() || productBrand.isEmpty() || productCategory.isEmpty()){
                Toast.makeText(this, "All field must be filled", Toast.LENGTH_LONG).show()
            }else if (productDescription.length < 10){
                Toast.makeText(this, "Description must more than 10 characters", Toast.LENGTH_LONG).show()
            }else{
                val product = Product("", productName, productBrand, productCategory, productDescription, "", Timestamp.now(), Timestamp.now())
                db.collection("products").add(product)
                    .addOnSuccessListener { documentReference ->
                        Log.d("hi", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("hi", "Error adding document", e)
                    }
//                val ref: StorageReference = storageReference
//                    .child(
//                        "images/"
//                                + UUID.randomUUID().toString()
//                    )

                // adding listeners on upload
                // or failure of image

                // adding listeners on upload
                // or failure of image
//                ref.putFile(filePath)
//                    .addOnSuccessListener(
//                        OnSuccessListener<Any?> { // Image uploaded successfully
//                            // Dismiss dialog
//                            progressDialog.dismiss()
//                            Toast
//                                .makeText(
//                                    this@MainActivity,
//                                    "Image Uploaded!!",
//                                    Toast.LENGTH_SHORT
//                                )
//                                .show()
//                        })
//                    .addOnFailureListener(OnFailureListener { e -> // Error, Image not uploaded
//                        progressDialog.dismiss()
//                        Toast
//                            .makeText(
//                                this@MainActivity,
//                                "Failed " + e.message,
//                                Toast.LENGTH_SHORT
//                            )
//                            .show()
//                    })
//                    .addOnProgressListener(
//                        OnProgressListener<Any> { taskSnapshot ->
//
//                            // Progress Listener for loading
//                            // percentage on the dialog box
//                            val progress: Double = (100.0
//                                    * taskSnapshot.getBytesTransferred()
//                                    / taskSnapshot.getTotalByteCount())
//                            progressDialog.setMessage(
//                                "Uploaded "
//                                        + progress.toInt() + "%"
//                            )
//                        })
            }

        }
    }
}
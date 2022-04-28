package edu.bluejack21_2.KZkin.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.activity.MainActivityAdmin
import edu.bluejack21_2.KZkin.model.Product
import java.io.IOException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InsertProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertProductFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1234
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var productPicture: String
    private var photoProduct: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val categories = resources.getStringArray(R.array.product_categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        val autocompleteTV = requireView().findViewById<AutoCompleteTextView>(R.id.autoCompleteProductCategoryTextView)
        autocompleteTV.setAdapter(arrayAdapter)

        val buttonSubmit = requireView().findViewById<Button>(R.id.buttonSubmitInsertProduct)
        val inputProductName = requireView().findViewById<TextInputLayout>(R.id.inputInsertProductName)
        val inputProductBrand = requireView().findViewById<TextInputLayout>(R.id.inputInsertProductBrand)
        val inputProductCategory = requireView().findViewById<TextInputLayout>(R.id.inputInsertProductCategory)
        val inputProductDescription = requireView().findViewById<TextInputLayout>(R.id.inputInsertProductDescription)
        val buttonProductImage = requireView().findViewById<Button>(R.id.buttonInsertProductImage)

        buttonProductImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.label_image)),
                PICK_IMAGE_REQUEST
            )
        }

        buttonSubmit.setOnClickListener {

            val productName = inputProductName.editText!!.text.toString()
            val productBrand = inputProductBrand.editText!!.text.toString()
            var productCategory = inputProductCategory.editText!!.text.toString()
            val productDescription = inputProductDescription.editText!!.text.toString()

            if (productName.isEmpty() || productBrand.isEmpty() || productCategory.isEmpty() || photoProduct!!.isEmpty()){
                Toast.makeText(requireContext(), getString(R.string.err_field_empty), Toast.LENGTH_LONG).show()
            }else if (productDescription.length < 10){
                Toast.makeText(requireContext(), getString(R.string.err_desc), Toast.LENGTH_LONG).show()
            }else{
                if(productCategory.equals("Perawatan Kulit")){
                    productCategory = "Skincare"
                }else if(productCategory.equals("Kosmetik")){
                    productCategory = "Make Up"
                }else if(productCategory.equals("Perawatan Rambut")){
                    productCategory = "Haircare"
                }else if(productCategory.equals("Wewangian")){
                    productCategory = "Fragrances"
                }
                val product = Product("", productName, productBrand, productCategory, productDescription,
                    photoProduct!!, 0.0f, 0, Timestamp.now(), Timestamp.now())
                db.collection("products").add(product)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(requireContext(), getString(R.string.succ_submit), Toast.LENGTH_SHORT).show()
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

                    Toast.makeText(requireContext(), getString(R.string.succ_img), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), getString(R.string.err_img), Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapShot ->
                    val progress =
                        100.00 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount

                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert_product, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InsertProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InsertProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
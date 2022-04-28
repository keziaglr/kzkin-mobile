package edu.bluejack21_2.KZkin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import com.google.android.material.textfield.TextInputLayout
import edu.bluejack21_2.KZkin.R

class FilterReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_review)
        val skins = resources.getStringArray(R.array.skin_type)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, skins)
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteFilterSkinTextView)
        autocompleteTV.setAdapter(arrayAdapter)

        val ratings = resources.getStringArray(R.array.label_rating)
        val arrayAdapter2 = ArrayAdapter(this, R.layout.dropdown_item, ratings)
        val autocompleteTV2= findViewById<AutoCompleteTextView>(R.id.autoCompleteRatingTextView)
        autocompleteTV2.setAdapter(arrayAdapter2)

        var btnSubmit = findViewById<Button>(R.id.buttonSubmitFilterReview)
        var btnReset = findViewById<Button>(R.id.buttonResetFilter)
        var inputAge = findViewById<TextInputLayout>(R.id.inputFilterAge)
        var inputSkin = findViewById<TextInputLayout>(R.id.inputFilterSkinType)
        var inputRating = findViewById<TextInputLayout>(R.id.inputFilterRating)
        var mostLiked = findViewById<CheckBox>(R.id.cbFilterMostLiked)
        var productId = intent.extras!!.get("id")
        btnSubmit.setOnClickListener{
            var age = inputAge.editText!!.text
            var sk = inputSkin.editText!!.text
            var skin = sk.toString()
            var like = mostLiked.isChecked
            var rating = inputRating.editText!!.text
            var intent = Intent(this, ProductDetailUserActivity::class.java)
            intent.putExtra("fAge", age.toString())
            if(skin.isNotEmpty() || skin.toString() != ""){
                if(skin.equals("Kulit Berminyak")){
                    skin = "Oily Skin"
                }else if(skin.equals("Kulit Kombinasi")){
                    skin = "Combination Skin"
                }else if(skin.equals("Kulit Kering")){
                    skin = "Dry Skin"
                }else if(skin.equals("Kulit Normal")){
                    skin = "Normal Skin"
                }
            }
            intent.putExtra("fSkin", skin)
            intent.putExtra("fLike", like.toString())
            intent.putExtra("fRating", rating.toString())

            Log.e("FILTERR", "AGE ${age.toString()} SKIN ${skin.toString()} LIKE ${like.toString()} RATING ${rating.toString()}")
            intent.putExtra("id", productId.toString())
            Log.e("FILTER", age.toString())
            startActivity(intent)
        }

        btnReset.setOnClickListener{
            inputAge.editText!!.text = null
            inputSkin.editText!!.text = null
            inputRating.editText!!.text = null
            mostLiked.isChecked = false
        }
    }
}
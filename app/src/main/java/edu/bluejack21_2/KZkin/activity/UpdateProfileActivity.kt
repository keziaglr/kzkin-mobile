package edu.bluejack21_2.KZkin.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.model.User
import java.io.IOException
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {
    private var userDOB: Date? = null
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1234
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var profileImage: String? = ""
    private var auth = Firebase.auth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val skinType = resources.getStringArray(R.array.skin_type)
        val skinArray = ArrayAdapter(this, R.layout.dropdown_item, skinType)
        val skinTypeTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteSkinTypeTextViewRegister2)
        skinTypeTV.setAdapter(skinArray)

        val gender = resources.getStringArray(R.array.gender)
        val genderArray = ArrayAdapter(this, R.layout.dropdown_item, gender)
        val genderTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteGenderTextViewRegister2)
        genderTV.setAdapter(genderArray)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        var btnDOB = findViewById<Button>(R.id.buttonDOBUpdate)
        btnDOB!!.setOnClickListener{
            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        }
        datePicker.addOnPositiveButtonClickListener {
            var calendar : Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(it);
            Log.e("DOB", calendar.getTime().toString())
            userDOB = calendar.time
        }

        val inputName = findViewById<TextInputLayout>(R.id.inputUpdateUserName)
        val inputPhoneNumber = findViewById<TextInputLayout>(R.id.inputUpdatePhoneNumber)
        val inputSkinType = findViewById<TextInputLayout>(R.id.inputUpdateSkinType)
        val inputGender = findViewById<TextInputLayout>(R.id.inputUpdateGender)
        val buttonProductImage = findViewById<Button>(R.id.buttonUpdateUserImage)

        buttonProductImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_image)), PICK_IMAGE_REQUEST)
        }

        db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {

        var user = it.toObject(User::class.java)
            if(user != null){
                inputName.editText!!.setText(user.name)
                inputPhoneNumber.editText!!.setText(user.phoneNumber)
                inputSkinType.editText!!.setText(user.skinType)
                inputGender.editText!!.setText(user.gender)
                profileImage = user.image
                userDOB = user.dob
                val buttonRegister = findViewById<TextView>(R.id.buttonSubmitUpdateProfile)
                    buttonRegister.setOnClickListener {
                        val name = inputName.editText!!.text.toString()
                        val phoneNumber = inputPhoneNumber.editText!!.text.toString()
                        var skinType = inputSkinType.editText!!.text.toString()
                        var gender = inputGender.editText!!.text.toString()

                        if (userDOB == null || name.isEmpty() || phoneNumber.isEmpty() || skinType.isEmpty() || gender.isEmpty() || profileImage!!.isEmpty()) {
                            Toast.makeText(this, getString(R.string.err_field_empty), Toast.LENGTH_LONG)
                                .show()
                        } else {
                            val monthNumber = DateFormat.format("MM", userDOB)
                            val yearNumber = DateFormat.format("yyyy", userDOB)
                            val dayNumber = DateFormat.format("dd", userDOB)
                            if (name.length < 5) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.err_name),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (phoneNumber.length < 12) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.err_phone),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (getAge(
                                    Integer.parseInt(yearNumber.toString()),
                                    Integer.parseInt(monthNumber.toString()),
                                    Integer.parseInt(dayNumber.toString())
                                )!! < 17
                            ) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.err_age),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if(skinType.equals("Kulit Berminyak")){
                                    skinType = "Oily Skin"
                                }else if(skinType.equals("Kulit Kombinasi")){
                                    skinType = "Combination Skin"
                                }else if(skinType.equals("Kulit Kering")){
                                    skinType = "Dry Skin"
                                }else if(skinType.equals("Kulit Normal")){
                                    skinType = "Normal Skin"
                                }

                                if(gender.equals("Perempuan")){
                                    gender = "Female"
                                }else  if(gender.equals("Laki-laki")){
                                    gender = "Male"
                                }
                                val user = User(
                                    "",
                                    name,
                                    user.password,
                                    phoneNumber,
                                    user.email,
                                    userDOB,
                                    skinType,
                                    gender,
                                    profileImage,
                                    user.role,
                                    user.createdAt,
                                    Timestamp.now()
                                )
                                db.collection("users").document(auth.currentUser!!.uid).set(user).addOnSuccessListener {
                                    Toast.makeText(
                                        applicationContext, getString(R.string.succ_submit),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    var goToHome: Intent? = null
                                    if (user!!.role == "admin") {
                                        goToHome = Intent(this, MainActivityAdmin::class.java)
                                    } else {
                                        goToHome = Intent(this, MainActivityUser::class.java)
                                    }

                                    startActivity(goToHome)
                                } }
                            }
                    }
                }
            }
        }
    private fun getAge(year: Int, month: Int, day: Int): Int? {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        Log.e("NOW", today[Calendar.YEAR].toString())
        Log.e("BDAY ", dob[Calendar.YEAR].toString())
        Log.e("selisih", age.toString())
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt
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
            val imageRef = storageReference!!.child("user/" + UUID.randomUUID().toString())
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        profileImage = it.toString()
                    }

                    Toast.makeText(
                        applicationContext,
                        getString(R.string.succ_img),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.err_img),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnProgressListener { taskSnapShot ->
                    val progress =
                        100.00 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount

                }
        }
    }
}

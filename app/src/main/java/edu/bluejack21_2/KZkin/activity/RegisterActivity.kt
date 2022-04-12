package edu.bluejack21_2.KZkin.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
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


class RegisterActivity : AppCompatActivity() {
    private var userDOB: Date? = null
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1234
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var profileImage: String? = ""
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        val skinType = resources.getStringArray(R.array.skin_type)
        val skinArray = ArrayAdapter(this, R.layout.dropdown_item, skinType)
        val skinTypeTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteSkinTypeTextViewRegister)
        skinTypeTV.setAdapter(skinArray)

        val gender = resources.getStringArray(R.array.gender)
        val genderArray = ArrayAdapter(this, R.layout.dropdown_item, gender)
        val genderTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteGenderTextViewRegister)
        genderTV.setAdapter(genderArray)

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        var btnDOB = findViewById<Button>(R.id.buttonDOBRegister)
        btnDOB!!.setOnClickListener{
            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        }
        datePicker.addOnPositiveButtonClickListener {
            var calendar : Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(it);
            Log.e("DOB", calendar.getTime().toString())
            userDOB = calendar.time
//            Log.e("MONTH", monthNumber.toString())
//            Log.e("AGE", getAge(Integer.parseInt(yearNumber.toString()) , Integer.parseInt(monthNumber.toString()), Integer.parseInt(dayNumber.toString())).toString())
        }

        val linkLogin = findViewById<TextView>(R.id.link_login)
        linkLogin!!.setOnClickListener{
            val goToLogin = Intent(applicationContext, LoginActivity::class.java)
            startActivity(goToLogin)
        }


        val db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val inputName = findViewById<TextInputLayout>(R.id.inputRegisterName)
        val inputPassword = findViewById<TextInputLayout>(R.id.inputRegisterPassword)
        val inputPhoneNumber = findViewById<TextInputLayout>(R.id.inputRegisterPhoneNumber)
        val inputEmail = findViewById<TextInputLayout>(R.id.inputRegisterEmail)
        val inputSkinType = findViewById<TextInputLayout>(R.id.inputRegisterSkinType)
        val inputGender = findViewById<TextInputLayout>(R.id.inputRegisterGender)
        val buttonProductImage = findViewById<Button>(R.id.buttonRegisterUserImage)

        buttonProductImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_image)), PICK_IMAGE_REQUEST)
        }

        val buttonRegister = findViewById<TextView>(R.id.buttonSubmitRegister)
        buttonRegister.setOnClickListener {
            val name = inputName.editText!!.text.toString()
            val password = inputPassword.editText!!.text.toString()
            val phoneNumber = inputPhoneNumber.editText!!.text.toString()
            val email = inputEmail.editText!!.text.toString()
            val skinType = inputSkinType.editText!!.text.toString()
            val gender = inputGender.editText!!.text.toString()
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            if (userDOB == null || name.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || skinType.isEmpty() || gender.isEmpty() || profileImage!!.isEmpty()) {
                Toast.makeText(this, getString(R.string.err_field_empty), Toast.LENGTH_LONG).show()
            }else {
                val monthNumber = DateFormat.format("MM", userDOB)
                val yearNumber = DateFormat.format("yyyy", userDOB)
                val dayNumber = DateFormat.format("dd", userDOB)
                if(name.length < 5) {
                    Toast.makeText(applicationContext, getString(R.string.err_name),Toast.LENGTH_SHORT).show()
                }else if(password.length < 8) {
                    Toast.makeText(applicationContext, getString(R.string.err_pass),Toast.LENGTH_SHORT).show()
                }else if(phoneNumber.length < 12) {
                    Toast.makeText(applicationContext, getString(R.string.err_phone),Toast.LENGTH_SHORT).show()
                }else if(!(email.matches(emailPattern.toRegex()))) {
                    Toast.makeText(applicationContext, getString(R.string.err_email_format),Toast.LENGTH_SHORT).show()
                }else if(getAge(Integer.parseInt(yearNumber.toString()) , Integer.parseInt(monthNumber.toString()), Integer.parseInt(dayNumber.toString()))!! < 17){
                    Toast.makeText(applicationContext, getString(R.string.err_age),Toast.LENGTH_SHORT).show()
                }else{
                    val user = User("", name, password, phoneNumber, email, userDOB, skinType, gender, profileImage, "user", Timestamp.now(), Timestamp.now())
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(applicationContext, getString(R.string.succ_regis),Toast.LENGTH_SHORT).show()
                                db.collection("users").document(auth.currentUser!!.uid).set(user, SetOptions.merge())
                                val goToHome = Intent(applicationContext, Home::class.java)
                                startActivity(goToHome)
                            } else {
                                Toast.makeText(baseContext, getString(R.string.err_regis), Toast.LENGTH_SHORT).show()
                            }
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
            val imageRef = storageReference!!.child("product/" + UUID.randomUUID().toString() )
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        profileImage = it.toString()
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
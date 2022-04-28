package edu.bluejack21_2.KZkin.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.activity.LoginActivity
import edu.bluejack21_2.KZkin.activity.UpdateProfileActivity
import edu.bluejack21_2.KZkin.model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileInformationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var name = view.findViewById<TextView>(R.id.viewUserName)
        var email = view.findViewById<TextView>(R.id.viewUserEmail)
        var phone = view.findViewById<TextView>(R.id.viewUserPhoneNumber)
        var gender = view.findViewById<TextView>(R.id.viewUserGender)
        var skintype = view.findViewById<TextView>(R.id.viewUserSkinType)
        var dob = view.findViewById<TextView>(R.id.viewUserDOB)
        var profileImage = view.findViewById<ImageView>(R.id.viewProfileImage)
        var btnUpdate = view.findViewById<Button>(R.id.buttonUpdateProfile)
        var btnLogout = view.findViewById<Button>(R.id.buttonLogout)

        btnUpdate.setOnClickListener {
            var intent = Intent(context, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            Log.e("user now", auth.currentUser?.uid.toString())
            auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("109122604925-vc57vh7nfl1edh697c37b0uudprnlg73.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(view.context, gso)
            googleSignInClient.signOut()
            var intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            Log.e("user now", auth.currentUser?.uid.toString())
            this.activity?.finish()
        }
        db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
            var user = it.toObject(User::class.java)
            if(user != null){
                name.setText(user.name)
                email.setText(user.email)
                if(user.phoneNumber == null) phone.setText("-")
                else phone.setText(user.phoneNumber)
                if(user.gender == "") gender.setText("-")
                else gender.setText(user.gender)
                if(user.skinType == "") skintype.setText("-")
                else skintype.setText(user.skinType)
                if(user.dob != null){
                    var dob1 = DateFormat.format("dd MMMM yyyy", user.dob).toString()
                    dob.setText(dob1)
                }else{
                    dob.setText("-")
                }

                val requestOption = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

                Glide.with(view)
                    .applyDefaultRequestOptions(requestOption)
                    .load(user.image)
                    .into(profileImage)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun logout() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileInformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
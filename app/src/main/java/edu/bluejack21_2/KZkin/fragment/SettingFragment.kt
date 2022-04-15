package edu.bluejack21_2.KZkin.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.model.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val db = Firebase.firestore
    private val auth = Firebase.auth

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
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var countReview = 0
        var countLikes = 0
        var review = view.findViewById<TextView>(R.id.viewCreatedReview)
        var likes = view.findViewById<TextView>(R.id.viewTotalLike)
        db.collection("reviews").whereEqualTo("userId", auth.currentUser!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                Log.e("DATA", auth.currentUser!!.uid)
                for (doc in it.result){
                    Log.e("DATA", doc.data.toString())
                    countReview++
                }
                review.setText(countReview.toString())
            }
        }

        db.collection("likes").whereEqualTo("userId", auth.currentUser!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                for (doc in it.result){
                    countLikes++
                }
                likes.setText(countLikes.toString())
            }
        }

        var btnDelete = view.findViewById<TextView>(R.id.buttonDeleteAccount)
        btnDelete.setOnClickListener{
            db.collection("users").document(auth.currentUser!!.uid).delete()
            var revRef = db.collection("reviews")
            revRef.whereEqualTo("userId", auth.currentUser!!.uid).get().addOnCompleteListener {
                if(it.isSuccessful){
                    for (doc in it.result){
                        revRef.document(doc.id).delete()
                    }
                }
            }
            var likeRef = db.collection("likes")
            likeRef.whereEqualTo("userId", auth.currentUser!!.uid).get().addOnCompleteListener {
                if(it.isSuccessful){
                    for (doc in it.result){
                        likeRef.document(doc.id).delete()
                    }
                }
            }

//            var appSettingPreferences = context.getSharedPreferences("", 0)
//            isLarge = appSettingPreferences.getBoolean(GLOBALS.SETTINGS_LARGE_KEY, false)

        }

        var btnSave = view.findViewById<Button>(R.id.buttonSubmitSetting)
        var switch = view.findViewById<Switch>(R.id.switchFontSize)
        btnSave.setOnClickListener {
            Log.e("SWITCH", switch.isChecked.toString())
            when (switch.isChecked) {
                true -> {
                    val themeID: Int = R.style.Theme_KZkin_FontLarge
                    requireContext().setTheme(themeID)
                    Log.e("font", themeID.toString())
                }
                else -> {
                    val themeID: Int = R.style.Theme_KZkin
                    requireContext().setTheme(themeID)
                    Log.e("font", themeID.toString())
                }
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
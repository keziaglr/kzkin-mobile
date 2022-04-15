package edu.bluejack21_2.KZkin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.fragment.HomeFragment
import edu.bluejack21_2.KZkin.fragment.InsertProductFragment
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.fragment.NotificationFragment
import edu.bluejack21_2.KZkin.fragment.ProfileUserFragment
import edu.bluejack21_2.KZkin.model.User

class MainActivityUser : AppCompatActivity() {
    private val home = HomeFragment()
    private val notif = NotificationFragment()
    private val profile = ProfileUserFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_user)
        replacementFragment(home)
        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_user)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_1 -> replacementFragment(home)
                R.id.page_2 -> replacementFragment(notif)
                R.id.page_3 -> replacementFragment(profile)
            }
            true
        }
    }

    private fun replacementFragment(fragment : Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment)
            transaction.commit()
        }
    }
}
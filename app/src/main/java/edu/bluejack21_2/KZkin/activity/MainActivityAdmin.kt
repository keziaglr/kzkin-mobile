package edu.bluejack21_2.KZkin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.fragment.HomeFragment
import edu.bluejack21_2.KZkin.fragment.InsertProductFragment
import edu.bluejack21_2.KZkin.fragment.ProfileInformationFragment
import edu.bluejack21_2.KZkin.fragment.ProfileUserFragment
import edu.bluejack21_2.KZkin.model.User

class MainActivityAdmin : AppCompatActivity() {
    private val home = HomeFragment()
    private val insertProduct = InsertProductFragment()
    private val profile = ProfileInformationFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_admin)
        replacementFragment(home)
        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_admin)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.page_1_admin -> replacementFragment(home)
                R.id.page_2_admin -> replacementFragment(insertProduct)
                R.id.page_3_admin -> replacementFragment(profile)
            }
            true
        }
    }

     fun replacementFragment(fragment : Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container2, fragment)
            transaction.commit()
        }
    }
}
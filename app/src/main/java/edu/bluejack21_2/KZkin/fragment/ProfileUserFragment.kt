package edu.bluejack21_2.KZkin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.adapter.ViewPagerAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_profile_user, container, false)
    }

    fun setTab(view: View){
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(ProfileInformationFragment() , getString(R.string.tab_profile))
        adapter.addFragment(SettingFragment() , getString(R.string.activity))
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter
        val tabs = view.findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTab(view)
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
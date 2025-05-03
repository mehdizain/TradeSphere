package com.example.tradesphere

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tradesphere.fragments.HomeFragment
import com.example.tradesphere.fragments.SearchFragment
import com.example.tradesphere.fragments.AllChatsFragment
import com.example.tradesphere.fragments.AccountFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> SearchFragment()
            2 -> AllChatsFragment()
            3 -> AccountFragment()
            else -> HomeFragment()
        }
    }
}

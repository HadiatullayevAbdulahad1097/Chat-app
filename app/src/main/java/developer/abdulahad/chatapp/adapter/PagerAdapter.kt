package developer.abdulahad.chatapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import developer.abdulahad.chatapp.GroupsFragment
import developer.abdulahad.chatapp.UsersFragment

class PagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> UsersFragment()
            else -> GroupsFragment()
        }
    }
}

package developer.abdulahad.chatapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import developer.abdulahad.chatapp.Models.MyData
import developer.abdulahad.chatapp.Models.MyObject
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.adapter.PagerAdapter
import developer.abdulahad.chatapp.databinding.FragmentGroupAndUsersBinding
import developer.abdulahad.chatapp.databinding.ItemHeaderBinding
import developer.abdulahad.chatapp.databinding.ItemtabBinding

class GroupAndUsersFragment : Fragment() {
    lateinit var binding: FragmentGroupAndUsersBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var pagerAdapter: PagerAdapter
    lateinit var name:String
    lateinit var lastname:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupAndUsersBinding.inflate(layoutInflater)

        MyData.init(binding.root.context)

        if (android.os.Build.VERSION.SDK_INT >= 21){
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#2962FF")
        }

            pagerAdapter = PagerAdapter(this)

        binding.apply {
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

            val itemHeaderBinding = ItemHeaderBinding.inflate(layoutInflater)
            name = MyData.name!!.substring(1,MyData.name!!.indexOf("||/"))
            lastname = MyData.name!!.substring(MyData.name!!.indexOf("|/")+2,MyData.name!!.indexOf("//"))
            itemHeaderBinding.name.text = name
            itemHeaderBinding.lastname.text = lastname
            navigationView.addHeaderView(itemHeaderBinding.root)

            menu.setOnClickListener {
                drawerLayout.open()
            }
            viewPager2.adapter = pagerAdapter

            tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    var custom = tab?.customView
                    custom?.findViewById<LinearLayout>(R.id.linear)?.setBackgroundColor(Color.parseColor("#2AACFB"))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    var custom = tab?.customView
                    custom?.findViewById<LinearLayout>(R.id.linear)?.setBackgroundColor(Color.parseColor("#2962FF"))
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            var list = ArrayList<String>()
            list.add("Users")
            list.add("Groups")
            TabLayoutMediator(tab,viewPager2){tab,position->
                var item = ItemtabBinding.inflate(layoutInflater)

                item.linear.setBackgroundColor(Color.parseColor("#2962FF"))

                item.tvName.text = list[position]

                tab.customView = item.root

            }.attach()
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        MyObject.updateOnlineStatus("offline")
    }

    override fun onResume() {
        super.onResume()
        MyObject.updateOnlineStatus("online")
    }
}
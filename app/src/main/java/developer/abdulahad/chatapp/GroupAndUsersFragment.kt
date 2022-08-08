package developer.abdulahad.chatapp

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import developer.abdulahad.chatapp.Models.MyData
import developer.abdulahad.chatapp.Models.MyObject
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.adapter.PagerAdapter
import developer.abdulahad.chatapp.databinding.FragmentGroupAndUsersBinding
import developer.abdulahad.chatapp.databinding.ItemHeaderBinding
import developer.abdulahad.chatapp.databinding.ItemtabBinding
import java.time.LocalTime

class GroupAndUsersFragment : Fragment() {
    lateinit var binding: FragmentGroupAndUsersBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var pagerAdapter: PagerAdapter
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storeReferences: StorageReference
    lateinit var itemHeaderBinding: ItemHeaderBinding
    lateinit var firebaseAuth: FirebaseAuth
    private var imagePath = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupAndUsersBinding.inflate(layoutInflater)

        MyData.init(binding.root.context)
        firebaseStorage = FirebaseStorage.getInstance()
        storeReferences = firebaseStorage.getReference("images")
        itemHeaderBinding = ItemHeaderBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.parseColor("#2962FF")

        pagerAdapter = PagerAdapter(this)

        binding.apply {
            firebaseDatabase = FirebaseDatabase.getInstance()
            reference = firebaseDatabase.getReference("users")

            reference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children){
                        var value = child.getValue(User::class.java)
                        if (value!=null){
                            if (value.uid == firebaseAuth.uid) {
                                itemHeaderBinding.name.text = value.name
                                Glide.with(binding.root.context).load(value.imageUri)
                                    .into(itemHeaderBinding.image)
                                navigationView.addHeaderView(itemHeaderBinding.root)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            menu.setOnClickListener {
                drawerLayout.open()
            }
            viewPager2.adapter = pagerAdapter

            tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    var custom = tab?.customView
                    custom?.findViewById<LinearLayout>(R.id.linear)
                        ?.setBackgroundColor(Color.parseColor("#2AACFB"))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    var custom = tab?.customView
                    custom?.findViewById<LinearLayout>(R.id.linear)
                        ?.setBackgroundColor(Color.parseColor("#2962FF"))
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            var list = ArrayList<String>()
            list.add("Users")
            list.add("Groups")
            TabLayoutMediator(tab, viewPager2) { tab, position ->
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
        MyObject.updateTime(LocalTime.now().toString().substring(0,5))
    }

    override fun onResume() {
        super.onResume()
        MyObject.updateOnlineStatus("online")
    }
}
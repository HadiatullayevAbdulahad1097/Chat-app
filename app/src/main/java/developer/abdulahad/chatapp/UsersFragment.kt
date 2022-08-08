package developer.abdulahad.chatapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import developer.abdulahad.chatapp.Models.MyLiveData
import developer.abdulahad.chatapp.Models.MyObject
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.adapter.RvAdapter
import developer.abdulahad.chatapp.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {
    lateinit var binding: FragmentUsersBinding
    lateinit var auth: FirebaseAuth
    lateinit var rvAdapter: RvAdapter
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var list: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("users")

        list = ArrayList()
        rvAdapter = RvAdapter(binding.root.context,list,auth,object : RvAdapter.ClickInterface{
            override fun clickItem(user: User, position: Int) {
                findNavController().navigate(R.id.chatsFragment, bundleOf("keyUser" to user,"lichka" to true))
            }
        })
        binding.rv.adapter = rvAdapter

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value!=null){
                        list.add(value)
                    }
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }
}
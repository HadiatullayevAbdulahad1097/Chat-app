package developer.abdulahad.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import developer.abdulahad.chatapp.Models.MyMessage
import developer.abdulahad.chatapp.Models.MyObject
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.databinding.FragmentGroupsBinding

class GroupsFragment : Fragment() {
    lateinit var binding: FragmentGroupsBinding
    lateinit var list: ArrayList<User>
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(layoutInflater)

        list = ArrayList()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("users")

        binding.cardRv.setOnClickListener {
            MyObject.list = list
            findNavController().navigate(R.id.chatsFragment, bundleOf("lichka" to false))
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (i in snapshot.children) {
                    val value = i.getValue(User::class.java)
                    list.add(value!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return binding.root
    }
}
package developer.abdulahad.chatapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import developer.abdulahad.chatapp.Models.*
import developer.abdulahad.chatapp.adapter.MessageAdapter
import developer.abdulahad.chatapp.databinding.FragmentChatsBinding
import kotlinx.coroutines.Runnable

class ChatsFragment : Fragment() {
    lateinit var binding: FragmentChatsBinding
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    lateinit var list: ArrayList<MyMessage>
    private var user: User? = null
    var lichka = false
    private var myMessage = MyMessage()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        loadFirebase()

        lichka = arguments?.getBoolean("lichka")!!
        if (lichka) {
            user = arguments?.getSerializable("keyUser") as User
        }
        if (!lichka) {
            binding.name.text = MyObject.user.name
            binding.name.textSize = 18f
        } else {
            binding.name.text = "${user?.name} ${user?.lastName}"
            binding.image.setImageResource(user!!.image)
            if (user!!.online == "online") {
                binding.online.text = "online"
                binding.online.setTextColor(Color.parseColor("#09B851"))
            } else {
                binding.online.setTextColor(Color.WHITE)
                binding.online.text = "offline"
            }
        }

        binding.send.setOnClickListener {
            var text = binding.chats.text.toString()
            if (text.isNotEmpty() && text != " " && text != "  " && text != "   ") {
                if (lichka) {
                    myMessage = MyMessage(text.trim(), auth.uid, user?.uid)
                } else {
                    myMessage =
                        MyMessage(text.trim(), auth.uid, "gruppaga")
                }
            } else {
                Toast.makeText(context, "Text isEmpty", Toast.LENGTH_SHORT)
                    .show()
            }
            val id = reference.push().key
            reference.child(id!!).setValue(myMessage)
            Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
            binding.chats.text.clear()
        }
        return binding.root
    }

    private fun loadFirebase() {
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("messages")

        list = ArrayList()
        messageAdapter = MessageAdapter(auth, list)
        binding.rv.adapter = messageAdapter


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                var children = snapshot.children
                for (child in children) {
                    val value = child.getValue(MyMessage::class.java)
                    if (value != null) {
                        if (lichka) {
                            if ((value.fromId == auth.uid && user!!.uid == value.toId) || (auth.uid == value.toId && user!!.uid == value.fromId)) {
                                list.add(value)
                            }
                        } else {
                            if (value.toId == "gruppaga" || value.fromId == "gruppaga") {
                                list.add(value)
                            }
                        }
                    }
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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
package developer.abdulahad.chatapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private var user: User? = null
    private var imagePath: Uri? = null
    lateinit var list2: ArrayList<String>
    var lichka = false
    private var myMessage = MyMessage()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        list = ArrayList()
        list2 = ArrayList()
        lichka = arguments?.getBoolean("lichka")!!
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("messages")
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReference("image")


        binding.imagePackages.setOnClickListener {
            getImageContent.launch("*/*")
        }

        var type = 0
        if (imagePath?.toString()!!.contains("audio")) {
            type = 1
        } else if (imagePath?.toString()!!.contains("video")) {
            type = 2
        } else {
            type = 0
        }

        if (lichka) {
            user = arguments?.getSerializable("keyUser") as User
            messageAdapter = MessageAdapter(auth, list)
            binding.name.text = user!!.name
            Glide.with(binding.root.context).load(user!!.imageUri).into(binding.image)
            binding.online.text = user!!.online
        } else {
            messageAdapter = MessageAdapter(auth, list)
            binding.name.text = "Groups"
            binding.name.textSize = 18f
        }
        loadFirebase()

        binding.send.setOnClickListener {
            var text = binding.chats.text.toString()
            if (text.isNotEmpty() && text != " " && text != "  " && text != "   ") {
                if (lichka) {
                    myMessage = MyMessage(text.trim(), auth.uid, user?.uid, user?.name)
                } else {
                    var a = "Group"
                    for (i in MyObject.list) {
                        if (i.uid == auth.uid) {
                            a = i.name!!
                            break
                        }
                    }
                    myMessage =
                        MyMessage(text.trim(), auth.uid, "gruppaga", a)
                    val id = reference.push().key
                    reference.child(id!!).setValue(myMessage)
                    Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Text isEmpty", Toast.LENGTH_SHORT)
                    .show()
            }
//image
            val hours = System.currentTimeMillis()

            val tesk = storageReference.child(hours.toString()).putFile(imagePath!!)
            binding.chats.text.clear()

            tesk.addOnSuccessListener {
                if (it.task.isSuccessful) {

                    val downloadUrl = it.metadata?.reference?.downloadUrl

                    downloadUrl?.addOnSuccessListener { uri ->
                        if (lichka) {
                            myMessage =
                                MyMessage(
                                    uri.toString(),
                                    auth.uid,
                                    user?.uid,
                                    user!!.name,
                                    type
                                )
                        } else {
                            myMessage =
                                MyMessage(
                                    uri.toString(),
                                    auth.uid,
                                    "gruppaga",
                                    user!!.name,
                                    type
                                )
                        }
                        val id1 = reference.push().key
                        reference.child(id1!!).setValue(myMessage)
                        Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
                        imagePath = null
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun loadFirebase() {

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
                                value.name = user!!.name
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

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePath = uri
        }
}
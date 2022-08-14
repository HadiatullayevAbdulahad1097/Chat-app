package developer.abdulahad.chatapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import developer.abdulahad.chatapp.Models.*
import developer.abdulahad.chatapp.adapter.MessageAdapter
import developer.abdulahad.chatapp.databinding.BottomItemBinding
import developer.abdulahad.chatapp.databinding.FragmentChatsBinding
import kotlinx.coroutines.Runnable
import java.net.URI

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
    var lichka = false
    private var myMessage = MyMessage()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)

        MyObject.updateOnlineStatus("online")

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("messages")
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReference("image")
        list = ArrayList()
        lichka = arguments?.getBoolean("lichka")!!


        binding.imagePackages.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(binding.root.context)
            val bottomItemBinding = BottomItemBinding.inflate(layoutInflater)
            bottomItemBinding.image.setOnClickListener {
                bottomSheetDialog.cancel()
                getImageContent.launch("image/*")
            }
            bottomItemBinding.music.setOnClickListener {
                bottomSheetDialog.cancel()
                getMusicContent.launch("audio/*")
            }
            bottomItemBinding.video.setOnClickListener {
                bottomSheetDialog.cancel()
                getVideoContent.launch("video/*")
            }
            bottomSheetDialog.setContentView(bottomItemBinding.root)
            bottomSheetDialog.show()
        }


        if (lichka) {
            user = arguments?.getSerializable("keyUser") as User
            binding.name.text = user!!.name
            Glide.with(binding.root.context).load(user!!.imageUri).into(binding.image)
            binding.online.text = user!!.online
            messageAdapter = MessageAdapter(auth, list)
        } else {

            binding.name.text = "Groups"
            binding.name.textSize = 18f
            messageAdapter = MessageAdapter(auth, list)
        }
        loadFirebase()

        binding.send.setOnClickListener {
            var text = binding.chats.text.toString()
            if (text.isNotEmpty() && text != " " && text != "  " && text != "   ") {
                if (lichka) {
                    myMessage = MyMessage(text.trim(), auth.uid, user?.uid, user?.name, 0)
                } else {
                    var a = "Group"
                    for (i in MyObject.list) {
                        if (i.uid == auth.uid) {
                            a = i.name!!
                            break
                        }
                    }
                    myMessage =
                        MyMessage(text.trim(), auth.uid, "gruppaga", a, 0)
                }
                val id = reference.push().key
                reference.child(id!!).setValue(myMessage)
                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
                binding.chats.text.clear()
            } else {
                Toast.makeText(context, "Text isEmpty", Toast.LENGTH_SHORT)
                    .show()
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

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it ?: return@registerForActivityResult
            val hours = System.currentTimeMillis()

            val tesk = storageReference.child(hours.toString()).putFile(it)

            tesk.addOnSuccessListener {


                val downloadUrl = it.metadata?.reference?.downloadUrl

                downloadUrl?.addOnSuccessListener { url ->
                    val message = if (lichka) {
                        MyMessage(
                            fromId = auth.uid,
                            toId = user?.uid,
                            name = user?.name,
                            type = 1,
                            storage = url.toString()
                        )
                    } else {
                        var a = "Group"
                        for (i in MyObject.list) {
                            if (i.uid == auth.uid) {
                                a = i.name!!
                                break
                            }
                        }
                        MyMessage(fromId = auth.uid, toId =  "gruppaga", name =  a, type =  1, storage = url.toString())
                    }
                    val id = reference.push().key
                    reference.child(id!!).setValue(message)
                    Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val getMusicContent = registerForActivityResult(ActivityResultContracts.GetContent()){url->
        url ?: return@registerForActivityResult

        val hours = System.currentTimeMillis()

        val tesk = storageReference.child(hours.toString()).putFile(url)

        tesk.addOnSuccessListener {


            val downloadUrl = it.metadata?.reference?.downloadUrl

            downloadUrl?.addOnSuccessListener { uri ->
                val message = if (lichka) {
                    MyMessage(
                        fromId = auth.uid,
                        toId = user?.uid,
                        name = user?.name,
                        type = 2,
                        storage = uri.toString()
                    )
                } else {
                    var a = "Group"
                    for (i in MyObject.list) {
                        if (i.uid == auth.uid) {
                            a = i.name!!
                            break
                        }
                    }
                    MyMessage(fromId = auth.uid, toId =  "gruppaga", name =  a, type =  2, storage = uri.toString())
                }
                val id = reference.push().key
                reference.child(id!!).setValue(message)
                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
    private val getVideoContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        it ?: return@registerForActivityResult

        val hours = System.currentTimeMillis()

        val tesk = storageReference.child(hours.toString()).putFile(it)

        tesk.addOnSuccessListener {


            val downloadUrl = it.metadata?.reference?.downloadUrl

            downloadUrl?.addOnSuccessListener { uri ->
                val message = if (lichka) {
                    MyMessage(
                        fromId = auth.uid,
                        toId = user?.uid,
                        name = user?.name,
                        type = 3,
                        storage = uri.toString()
                    )
                } else {
                    var a = "Group"
                    for (i in MyObject.list) {
                        if (i.uid == auth.uid) {
                            a = i.name!!
                            break
                        }
                    }
                    MyMessage(fromId = auth.uid, toId =  "gruppaga", name =  a, type =  3, storage = uri.toString())
                }
                val id = reference.push().key
                reference.child(id!!).setValue(message)
                Toast.makeText(context, "Send", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}

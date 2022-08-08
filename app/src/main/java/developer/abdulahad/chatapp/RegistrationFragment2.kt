package developer.abdulahad.chatapp

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.databinding.FragmentRegistration2Binding
import developer.abdulahad.chatapp.databinding.FragmentRegistrationBinding

class RegistrationFragment2 : Fragment() {
    lateinit var binding: FragmentRegistration2Binding
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var list: ArrayList<User>
    lateinit var user: User
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var imagePath: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistration2Binding.inflate(layoutInflater)


        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.parseColor("#0091EA")

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReference("images")

        list = ArrayList()

        imagePath = ""

        binding.apply {
            image.setOnClickListener {
                getImageContent.launch("image/*")
            }
            btnSend.setOnClickListener {
                val name = edtName.text!!.toString()
                val email = "${edtEmail.text!!}@gmail.com"
                val password = edtLogin.text!!.toString()
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && imagePath.isNotEmpty()) {
                    if (list.isNotEmpty()){
                    for (i in list) {
                        if (i.email != email) {
                        sign(name, email, password)
                        } else {
                            Toast.makeText(context, "This Email Available", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    }else{
                        sign(name, email, password)
                        Toast.makeText(context, "list is empty", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "EditText or Image is Blank", Toast.LENGTH_SHORT).show()
                }
            }

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (child in snapshot.children) {
                        var value = child.getValue(User::class.java)
                        if (value != null) {
                            list.add(value)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

        return binding.root
    }

    fun sign(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user = User(
                        auth.uid,
                        name,
                        email,
                        password,
                        imagePath
                    )
                    reference.child(auth.uid!!).setValue(user)
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.groupAndUsersFragment)
                    Toast.makeText(binding.root.context, "Successful", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(binding.root.context, "Failed or @gmail.com is available", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private var getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            val hours = System.currentTimeMillis()

            val tesk = storageReference.child(hours.toString()).putFile(uri!!)

            tesk.addOnSuccessListener {


                val downloadUrl = it.metadata?.reference?.downloadUrl

                downloadUrl?.addOnSuccessListener { uri ->
                    imagePath = uri.toString()
                    Glide.with(binding.root.context).load(imagePath).into(binding.image)
                }

            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
}
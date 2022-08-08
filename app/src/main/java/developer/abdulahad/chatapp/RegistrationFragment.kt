package developer.abdulahad.chatapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import developer.abdulahad.chatapp.Models.MyData
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.databinding.FragmentRegistrationBinding
import java.util.concurrent.TimeUnit

class RegistrationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationBinding
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var list: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.parseColor("#0091EA")

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        list = ArrayList()

        binding.apply {
            btnLogin.setOnClickListener {
                val email = "${edtEmail.text!!}@gmail.com"
                val password = edtPasword.text!!.toString()
                if (email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
                    login(email, password)
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "Email or Password is Empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            btnSignIn.setOnClickListener {
                findNavController().navigate(R.id.registrationFragment2)
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

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.groupAndUsersFragment)
                    Toast.makeText(binding.root.context, "Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "Failed or @gmail.com is available",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }
}

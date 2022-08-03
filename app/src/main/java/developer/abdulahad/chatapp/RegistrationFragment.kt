package developer.abdulahad.chatapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import developer.abdulahad.chatapp.Models.MyObject
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.databinding.FragmentRegistrationBinding
import java.util.concurrent.TimeUnit

class RegistrationFragment : Fragment() {
    lateinit var binding: FragmentRegistrationBinding
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("users")
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00DDFF")
        }


        auth = FirebaseAuth.getInstance()
        MyObject.init(binding.root.context)
        MyData.init(binding.root.context)

        if (MyObject.list.isEmpty()) {
            binding.btnSend.setOnClickListener {
                try {
                    val number = binding.edtNumber.text.toString()
                    if (number.length == 13 &&
                        number.substring(0, 4) == "+998" &&
                        binding.edtName.text!!.isNotEmpty() &&
                        binding.edtLastname.text!!.isNotEmpty()
                    ) {
                        user = User()
                        user.name = binding.edtName.text.toString()
                        user.lastName = binding.edtLastname.text.toString()
                        user.number = binding.edtNumber.text.toString()

                        sendVerificationCode(number)
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Sms gacha yozgan malumotingizni tekshiring",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
//                else{
//                    resentCode(number)
//                }
                }catch (e:Exception){

                }
            }
            } else {
                findNavController().popBackStack()
                findNavController().navigate(R.id.groupAndUsersFragment)
            }

        binding.edtSmsCode.addTextChangedListener {
            if (it.toString().length == 6) {
                verifyCode()
            }
        }

        return binding.root
    }

    private fun verifyCode() {
        try {
            val credential =
                PhoneAuthProvider.getCredential(
                    storedVerificationId,
                    binding.edtSmsCode.text.toString()
                )
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            Toast.makeText(binding.root.context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(p0)
        }

        override fun onVerificationFailed(p0: FirebaseException) {

        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            storedVerificationId = p0
            resendToken = p1
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = MyObject.list
                    user.uid = auth.currentUser?.uid!!
                    databaseReference.child(auth.currentUser?.uid!!).setValue(user)
                    MyData.name = "|${user.name}||/${user.lastName}//."
                    Toast.makeText(binding.root.context, "Muvaffaqiyatli", Toast.LENGTH_SHORT)
                        .show()
                    list.clear()
                    list.add(MyObject.number)
                    MyObject.list = list
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.groupAndUsersFragment)
                } else {
                    Toast.makeText(binding.root.context, "Muvaffaqiyatsiz", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}
//
//    private fun resentCode(phoneNimber: String) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNimber)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(requireActivity())
//            .setCallbacks(callback)
//            .setForceResendingToken(resendToken)
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }

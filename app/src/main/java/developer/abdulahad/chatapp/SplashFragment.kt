package developer.abdulahad.chatapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import developer.abdulahad.chatapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    lateinit var binding: FragmentSplashBinding
    lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)

        if (android.os.Build.VERSION.SDK_INT >= 21){
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#FDB728")
        }

        binding.send.startAnimation(AnimationUtils.loadAnimation(binding.root.context,R.anim.alpha))
        binding.ellipse.startAnimation(AnimationUtils.loadAnimation(binding.root.context,R.anim.alpha))

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
        startAnimation(binding.send,1080f)
        },1500)

        handler.postDelayed(Runnable {
            binding.send.setImageResource(R.drawable.send)
            var anim = AnimationUtils.loadAnimation(binding.root.context,R.anim.anim)
            binding.send.startAnimation(anim)
            binding.send.visibility = View.INVISIBLE
            handler.postDelayed(Runnable {
                findNavController().popBackStack()
                findNavController().navigate(R.id.registrationFragment)
            },400)
        },2400)
        return binding.root
    }

    fun startAnimation(view: View, degree: Float) {
        var objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, degree)
        objectAnimator.duration = 1000
        var setAnimator = AnimatorSet()
        setAnimator.playTogether(objectAnimator)
        setAnimator.start()
    }

}
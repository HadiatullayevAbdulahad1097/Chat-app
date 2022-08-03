package developer.abdulahad.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import developer.abdulahad.chatapp.Models.MyMessage
import developer.abdulahad.chatapp.Models.MyObject
import developer.abdulahad.chatapp.databinding.FragmentGroupsBinding

class GroupsFragment : Fragment() {
    lateinit var binding: FragmentGroupsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(layoutInflater)

        binding.cardRv.setOnClickListener {
            MyObject.user.name = "Groups"
            findNavController().navigate(R.id.chatsFragment, bundleOf("lichka" to false))
        }

        return binding.root
    }
}
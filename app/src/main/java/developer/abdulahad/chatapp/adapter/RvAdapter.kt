package developer.abdulahad.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.R
import developer.abdulahad.chatapp.databinding.ItemRvBinding

class RvAdapter(var list: List<User>,var auth: FirebaseAuth,var clickInterface: ClickInterface,var isChat: Boolean) : RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root){
        fun onBind(user : User, position: Int) {
            itemRv.name.text = user.name
            itemRv.about.text = user.lastName
            if (auth.currentUser?.uid!! == user.uid){
                itemRv.image.setImageResource(R.drawable.ozim)
            }
                if (user.online == "online"){
                    itemRv.status.visibility = View.VISIBLE
                }else{
                    itemRv.status.visibility = View.GONE
                }

            itemRv.cardRv.setOnClickListener {
                clickInterface.clickItem(user, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position] , position)
    }

    override fun getItemCount(): Int = list.size
    interface ClickInterface{
        fun clickItem(user: User,position: Int)
    }
}